package uw.log.es.service;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.httpclient.http.HttpConfig;
import uw.httpclient.http.HttpHelper;
import uw.httpclient.http.HttpInterface;
import uw.httpclient.http.ObjectMapper;
import uw.httpclient.json.JsonInterfaceHelper;
import uw.httpclient.util.BufferRequestBody;
import uw.log.es.vo.ESDataList;
import uw.log.es.LogClientProperties;
import uw.log.es.vo.SearchResponse;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 日志服务
 *
 * @author liliang
 * @since 2018-04-25
 */
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private static final String INDEX_TYPE = "logs";

    /**
     * 读模式
     */
    private static final int READ_MODE = 1;

    /**
     * 读写模式
     */
    private static final int READ_WRITE_MODE = 2;

    /**
     * 日志编码
     */
    private static final Charset LOG_CHARSET = Charset.forName("UTF-8");

    /**
     * 换行符字节
     */
    public static final byte[] LINE_SEPARATOR_BYTES = System.getProperty("line.separator").getBytes(LOG_CHARSET);

    /**
     * httpInterface
     */
    private final HttpInterface httpInterface;

    /**
     * es集群地址
     */
    private final String clusters;

    /**
     * 用户名
     */
    private final String username;

    /**
     * 用户密码
     */
    private final String password;

    /**
     * 是否需要记录日志
     */
    private final boolean needLog;

    /**
     * 是否需要Http Basic验证头
     */
    private final boolean needBasicAuth;

    /**
     * Elasticsearch bulk api 地址
     */
    private String esBulk;

    /**
     * 模式
     */
    private int mode;

    /**
     * 刷新Bucket时间毫秒数
     */
    private long maxFlushInMilliseconds;

    /**
     * 允许最大Bucket 字节数。
     */
    private long minBytesOfBatch;

    /**
     * 最大批量线程数。
     */
    private int maxBatchThreads;

    /**
     * buffer
     */
    private okio.Buffer buffer = new okio.Buffer();

    /**
     * 读写锁
     */
    private final Lock batchLock = new ReentrantLock();

    /**
     * 后台线程
     */
    private ElasticsearchDaemonExporter daemonExporter;

    /**
     * 后台批量线程池。
     */
    private ThreadPoolExecutor batchExecutor;

    /**
     * 注册Mapping,<Class<?>,String>
     */
    private final Map<Class<?>,String> regMap = Maps.newHashMap();

    public LogService(final LogClientProperties logClientProperties) {
        this.clusters = logClientProperties.getEs().getClusters();
        if(StringUtils.isBlank(this.clusters)) {
            throw new RuntimeException("ES clusters must config");
        }
        this.username = logClientProperties.getEs().getUsername();
        this.password = logClientProperties.getEs().getPassword();
        this.needBasicAuth = StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
        this.httpInterface = new JsonInterfaceHelper(new HttpConfig.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(logClientProperties.getEs().getConnectTimeout())
                .readTimeout(logClientProperties.getEs().getReadTimeout())
                .writeTimeout(logClientProperties.getEs().getWriteTimeout())
                .build());
        this.esBulk = logClientProperties.getEs().getEsBulk();
        this.maxFlushInMilliseconds = logClientProperties.getEs().getMaxFlushInMilliseconds();
        this.minBytesOfBatch = logClientProperties.getEs().getMaxBytesOfBatch();
        this.maxBatchThreads = logClientProperties.getEs().getMaxBatchThreads();
        this.mode = logClientProperties.getEs().getMode();
        // 如果
        if (mode == READ_WRITE_MODE) {
            this.needLog = true;
            batchExecutor = new ThreadPoolExecutor(1, maxBatchThreads, 30, TimeUnit.SECONDS, new SynchronousQueue<>(),
                    new ThreadFactoryBuilder().setDaemon(true).setNameFormat("log-es-batch-%d").build(), new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    logger.error("log es Batch Task " + r.toString() + " rejected from " + executor.toString());
                }
            });

            daemonExporter = new ElasticsearchDaemonExporter();
            daemonExporter.init();
            daemonExporter.start();
        } else {
            this.needLog = false;
        }
    }

    /**
     * 将查询结果映射成List
     *
     * @param resp
     * @param tClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> mapQueryResponseToList(String resp,Class<T> tClass) {
        if (StringUtils.isNotBlank(resp)) {
            SearchResponse<T> response = null;
            try {
                response = ObjectMapper.DEFAULT_JSON_MAPPER.parse(resp,
                        ObjectMapper.DEFAULT_JSON_MAPPER
                                .constructParametricType(SearchResponse.class, tClass));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            if (response != null) {
                List<SearchResponse.Hits<T>> hitsList = response.getHisResponse().getHits();
                if (!hitsList.isEmpty()) {
                    List<T> dataList = Lists.newArrayList();
                    for (SearchResponse.Hits<T> hits : hitsList) {
                        dataList.add(hits.getSource());
                    }
                    return dataList;
                }
            }
        }
        return null;
    }

    /**
     * 将查询结果映射成EDataList
     *
     * @param resp
     * @param tClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> ESDataList<T> mapQueryResponseToEDataList(String resp, Class<T> tClass, int startIndex, int pageSize) {
        List<T> dataList = Lists.newArrayList();
        if (StringUtils.isNotBlank(resp)) {
            SearchResponse<T> response = null;
            try {
                response = ObjectMapper.DEFAULT_JSON_MAPPER.parse(resp,
                        ObjectMapper.DEFAULT_JSON_MAPPER
                                .constructParametricType(SearchResponse.class, tClass));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            if (response != null) {
                SearchResponse.HitsResponse<T> hitsResponse = response.getHisResponse();
                List<SearchResponse.Hits<T>> hitsList = hitsResponse.getHits();
                if (!hitsList.isEmpty()) {
                    for (SearchResponse.Hits<T> hits : hitsList) {
                        dataList.add(hits.getSource());
                    }
                    return new ESDataList<>(dataList,startIndex,pageSize,hitsResponse.getTotal());
                }
            }
        }
        return new ESDataList<>(dataList,startIndex,pageSize,0);
    }

    /**
     * 将查询结果映射成SearchResponse,便于应用组装分页
     *
     * @param resp
     * @param tClass
     * @param <T>
     * @return
     */
    private <T> SearchResponse<T> mapQueryResponseToSearchResponse(String resp,Class<?> tClass) {
        if (StringUtils.isBlank(resp)) {
            return null;
        }
        SearchResponse<T> response = null;
        try {
            response = ObjectMapper.DEFAULT_JSON_MAPPER.parse(resp,
                    ObjectMapper.DEFAULT_JSON_MAPPER
                            .constructParametricType(SearchResponse.class, tClass));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return response;
    }

    /**
     * 注册日志类型
     *
     * @param logClass
     */
    public void regLogObject(Class<?> logClass) {
        String className = logClass.getName();
        int lastIndex = className.lastIndexOf(".");
        String indexName = "";
        if (lastIndex > 0) {
            // 偏移一下,把'.'带上
            lastIndex++;
            String canonicalPath = className.substring(0, lastIndex);
            String logVoName = className.substring(lastIndex, className.length());
            indexName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, logVoName);
            indexName = canonicalPath + indexName;
        } else {
            indexName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
        }
        regMap.put(logClass,indexName);
    }

    /**
     * 查询日志索引
     *
     * @param logClass
     */
    public String getIndex(Class<?> logClass) {
        return regMap.get(logClass);
    }

    /**
     * 写日志
     *
     * @param source 日志对象
     */
    public void writeLog(Object source) {
        if (!needLog) {
            return;
        }
        String index = regMap.get(source.getClass());
        if (StringUtils.isBlank(index)) {
            return;
        }
        okio.Buffer okb = new okio.Buffer();
        okb.writeUtf8("{\"index\":{\"_index\":\"")
                .writeUtf8(index)
                .writeUtf8("\",\"_type\":\"")
                .writeUtf8(INDEX_TYPE)
                .writeUtf8("\"}}");
        okb.write(LINE_SEPARATOR_BYTES);
        try {
            ObjectMapper.DEFAULT_JSON_MAPPER.write(okb.outputStream(), source);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        okb.write(LINE_SEPARATOR_BYTES);
        batchLock.lock();
        try {
            buffer.writeAll(okb);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            batchLock.unlock();
        }
    }

    /**
     * 批量写日志
     *
     * @param sourceList 日志对象列表
     * @param <T>
     */
    public <T> void writeBulkLog(List<T> sourceList) {
        if (sourceList == null || sourceList.isEmpty()) {
            return;
        }
        if (!needLog) {
            return;
        }
        String index = regMap.get(sourceList.get(0).getClass());
        if (StringUtils.isBlank(index)) {
            return;
        }
        okio.Buffer okb = new okio.Buffer();
        for (T source : sourceList) {
            okb.writeUtf8("{\"index\":{\"_index\":\"")
                    .writeUtf8(index)
                    .writeUtf8("\",\"_type\":\"")
                    .writeUtf8(INDEX_TYPE)
                    .writeUtf8("\"}}");
            okb.write(LINE_SEPARATOR_BYTES);
            try {
                ObjectMapper.DEFAULT_JSON_MAPPER.write(okb.outputStream(), source);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            okb.write(LINE_SEPARATOR_BYTES);
        }
        batchLock.lock();
        try {
            buffer.writeAll(okb);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            batchLock.unlock();
        }
    }

    /**
     * Send buffer to Elasticsearch
     *
     * @param force - 是否强制发送
     */
    public void processLogBuffer(boolean force) {
        okio.Buffer bufferData = null;
        batchLock.lock();
        try {
            if (force || buffer.size() > minBytesOfBatch) {
                bufferData = buffer;
                buffer = new okio.Buffer();
            }
        } finally {
            batchLock.unlock();
        }
        if (bufferData == null || bufferData.size() == 0) {
            return;
        }
        try {
            Request.Builder requestBuilder = new Request.Builder().url(clusters + esBulk);
            if (needBasicAuth) {
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            httpInterface.requestForObject(requestBuilder.post(BufferRequestBody.create(HttpHelper.JSON_UTF8,
                    bufferData)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 关闭写日志系统
     */
    public void destroyLog() {
        if (needLog) {
            daemonExporter.readyDestroy();
            batchExecutor.shutdown();
            processLogBuffer(true);
        }
    }

    /**
     * 简单查询日志
     *
     * @param tClass 日志对象类型
     * @param index 索引
     * @param simpleQuery 简单查询条件
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> simpleQueryLog(Class<T> tClass,String index,String simpleQuery) {
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append(index).append("/")
                .append("_search?type=").append(INDEX_TYPE);
        if (StringUtils.isNotBlank(simpleQuery)) {
            urlBuilder.append("&").append(simpleQuery);
        }
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(needBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            resp = httpInterface.requestForObject(requestBuilder.get().build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return mapQueryResponseToList(resp,tClass);
    }

    /**
     * 简单日志查询
     *
     * @param tClass 日志对象类型
     * @param simpleQuery 简单查询条件
     * @param <T>
     * @return
     */
    public <T> SearchResponse<T> simpleQueryLogSearchResponse(Class<T> tClass,String index, String simpleQuery) {
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append(index).append("/")
                .append("_search?type=").append(INDEX_TYPE);
        if (StringUtils.isNotBlank(simpleQuery)) {
            urlBuilder.append("&").append(simpleQuery);
        }
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(needBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            resp = httpInterface.requestForObject(requestBuilder.get().build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return mapQueryResponseToSearchResponse(resp,tClass);
    }

    /**
     * dsl查询日志
     *
     * @param tClass 日志对象类型
     * @param index 索引
     * @param dslQuery dsl查询条件
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> dslQueryLog(Class<T> tClass,String index,String dslQuery) {
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append(index).append("/")
                .append("_search?type=").append(INDEX_TYPE);
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(needBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            resp = httpInterface.requestForObject(requestBuilder
                    .post(RequestBody.create(HttpHelper.JSON_UTF8,dslQuery)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return mapQueryResponseToList(resp,tClass);
    }


    /**
     * dsl查询日志
     *
     * @param tClass 日志对象类型
     * @param index 索引
     * @param dslQuery dsl查询条件
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> SearchResponse<T> dslQueryLogSearchResponse(Class<T> tClass,String index,String dslQuery) {
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append(index).append("/")
                .append("_search?type=").append(INDEX_TYPE);
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(needBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            resp = httpInterface.requestForObject(requestBuilder
                    .post(RequestBody.create(HttpHelper.JSON_UTF8,dslQuery)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return mapQueryResponseToSearchResponse(resp,tClass);
    }

    /**
     * dsl查询日志
     *
     * @param tClass 日志对象类型
     * @param sql sql
     * @return
     */
    public <T> ESDataList<T> sqlQueryLog(Class<T> tClass, String sql, int startIndex, int pageSize) {
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append("_sql?_type=").append(INDEX_TYPE);
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(needBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            resp = httpInterface.requestForObject(requestBuilder
                    .post(RequestBody.create(HttpHelper.JSON_UTF8,sql)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return mapQueryResponseToEDataList(resp,tClass,startIndex,pageSize);
    }

    /**
     * dsl查询日志
     *
     * @param tClass 日志对象类型
     * @param sql sql
     * @return
     */
    public <T> SearchResponse<T> sqlQueryLogSearchResponse(Class<T> tClass,String sql) {
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append("_sql?_type=").append(INDEX_TYPE);
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(needBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            resp = httpInterface.requestForObject(requestBuilder
                    .post(RequestBody.create(HttpHelper.JSON_UTF8,sql)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return mapQueryResponseToSearchResponse(resp,tClass);
    }

    /**
     * 后台写日志线程
     */
    public class ElasticsearchDaemonExporter extends Thread {

        /**
         * 运行标记.
         */
        private volatile boolean isRunning = false;

        /**
         * 下一次运行时间
         */
        private volatile long nextScanTime = 0;

        /**
         * 初始化
         */
        public void init() {
            isRunning = true;
        }

        /**
         * 销毁标记.
         */
        public void readyDestroy() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    if (nextScanTime < System.currentTimeMillis()) {
                        nextScanTime = System.currentTimeMillis() + maxFlushInMilliseconds;
                        batchExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                processLogBuffer(false);
                            }
                        });
                    }
                    Thread.sleep(500);
                } catch (Exception e) {
                    logger.error("Exception processing log entries", e);
                }
            }
        }
    }
}
