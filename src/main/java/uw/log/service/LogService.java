package uw.log.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import uw.log.LogClientProperties;
import uw.log.vo.SearchResponse;

import java.util.List;
import java.util.Map;

/**
 * 日志服务
 *
 * @author liliang
 * @since 2018-04-25
 */
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private static final String INDEX_TYPE = "logs";

    private final HttpInterface httpInterface;

    private final LogClientProperties logClientProperties;

    /**
     * 注册Mapping,<Class<?>,String>
     */
    private final Map<Class<?>,String> regMap = Maps.newHashMap();

    public LogService(final LogClientProperties logClientProperties) {
        this.httpInterface = new JsonInterfaceHelper(new HttpConfig.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(logClientProperties.getEsConfig().getConnectTimeout())
                .readTimeout(logClientProperties.getEsConfig().getReadTimeout())
                .writeTimeout(logClientProperties.getEsConfig().getWriteTimeout())
                .build());
        this.logClientProperties = logClientProperties;
    }

    /**
     * 将查询结果映射成List
     *
     * @param resp
     * @param tClass
     * @param <T>
     * @return
     */
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
     * 注册日志类型
     *
     * @param logClass
     */
    public void regLogObject(Class<?> logClass) {
        regMap.put(logClass,logClass.getName().toLowerCase());
    }

    /**
     * 写日志
     *
     * @param source 日志对象
     */
    public void writeLog(Object source) {
        if(StringUtils.isBlank(logClientProperties.getEsConfig().getClusters())) {
            return;
        }
        String index = regMap.get(source.getClass());
        if (StringUtils.isBlank(index)) {
            return;
        }
        StringBuilder urlBuilder = new StringBuilder(200);
        urlBuilder.append(logClientProperties.getEsConfig().getClusters())
                .append("/").append(index).append("/")
                .append(INDEX_TYPE);
        String resp = null;
        try {
            resp = ObjectMapper.DEFAULT_JSON_MAPPER.toString(source);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            httpInterface.requestForObject(new Request.Builder().url(urlBuilder.toString())
                    .post(RequestBody.create(HttpHelper.JSON_UTF8, resp)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage() + ",log: " + resp, e);
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
        if(StringUtils.isBlank(logClientProperties.getEsConfig().getClusters())) {
            return;
        }
        String index = regMap.get(sourceList.get(0).getClass());
        if (StringUtils.isBlank(index)) {
            return;
        }
        StringBuilder urlBuilder = new StringBuilder(50);
        urlBuilder.append(logClientProperties.getEsConfig().getClusters())
                .append("/").append("_bulk");
        okio.Buffer okBuffer = new okio.Buffer();
        try {
            for (T source : sourceList) {
                okBuffer.writeUtf8("{\"index\":{\"_index\":\"")
                        .writeUtf8(index)
                        .writeUtf8("\",\"_type\":\"")
                        .writeUtf8(INDEX_TYPE)
                        .writeUtf8("\"}}\n");
                ObjectMapper.DEFAULT_JSON_MAPPER.write(okBuffer.outputStream(), source);
                okBuffer.writeUtf8("\n");
            }
            httpInterface.requestForObject(new Request.Builder().url(urlBuilder.toString())
                    .post(BufferRequestBody.create(HttpHelper.JSON_UTF8, okBuffer)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage() + ",log: " + okBuffer.toString(), e);
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
        StringBuilder urlBuilder = new StringBuilder(logClientProperties.getEsConfig().getClusters());
        urlBuilder.append("/").append(index).append("/")
                .append("_search?type=").append(INDEX_TYPE);
        if (StringUtils.isNotBlank(simpleQuery)) {
            urlBuilder.append("&").append(simpleQuery);
        }
        String resp = null;
        try {
            resp = httpInterface.requestForObject(new Request.Builder().url(urlBuilder.toString())
                    .get().build(), String.class);
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
    public <T> List<T> dslQueryLog(Class<T> tClass,String index,String dslQuery) {
        StringBuilder urlBuilder = new StringBuilder(logClientProperties.getEsConfig().getClusters());
        urlBuilder.append("/").append(index).append("/")
                .append("_search?type=").append(INDEX_TYPE);
        String resp = null;
        try {
            resp = httpInterface.requestForObject(new Request.Builder().url(urlBuilder.toString())
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
     * @param sql sql
     * @return
     */
    public <T> List<T> sqlQueryLog(Class<T> tClass,String sql) {
        StringBuilder urlBuilder = new StringBuilder(logClientProperties.getEsConfig().getClusters());
        urlBuilder.append("/").append("_sql?_type=").append(INDEX_TYPE);
        String resp = null;
        try {
            resp = httpInterface.requestForObject(new Request.Builder().url(urlBuilder.toString())
                    .post(RequestBody.create(HttpHelper.JSON_UTF8,sql)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return mapQueryResponseToList(resp,tClass);
    }
}
