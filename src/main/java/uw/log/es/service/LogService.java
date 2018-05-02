package uw.log.es.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import uw.log.es.LogClientProperties;
import uw.log.es.vo.SearchResponse;

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
     * 是否需要Http Basic验证头
     */
    private final boolean isBasicAuth;

    /**
     * 注册Mapping,<Class<?>,String>
     */
    private final Map<Class<?>,String> regMap = Maps.newHashMap();

    public LogService(final LogClientProperties logClientProperties) {
        this.httpInterface = new JsonInterfaceHelper(new HttpConfig.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(logClientProperties.getEs().getConnectTimeout())
                .readTimeout(logClientProperties.getEs().getReadTimeout())
                .writeTimeout(logClientProperties.getEs().getWriteTimeout())
                .build());
        this.clusters = logClientProperties.getEs().getClusters();
        this.username = logClientProperties.getEs().getUsername();
        this.password = logClientProperties.getEs().getPassword();
        this.isBasicAuth = StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
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
        regMap.put(logClass,logClass.getName().toLowerCase());
    }

    /**
     * 查询日志索引
     *
     * @param logClass
     * @return
     */
    public String getLogObjectIndex(Class<?> logClass) {
        return regMap.get(logClass);
    }

    /**
     * 写日志
     *
     * @param source 日志对象
     */
    public void writeLog(Object source) {
        if(StringUtils.isBlank(clusters)) {
            return;
        }
        String index = regMap.get(source.getClass());
        if (StringUtils.isBlank(index)) {
            return;
        }
        StringBuilder urlBuilder = new StringBuilder(200);
        urlBuilder.append(clusters)
                .append("/").append(index).append("/")
                .append(INDEX_TYPE);
        String resp = null;
        try {
            resp = ObjectMapper.DEFAULT_JSON_MAPPER.toString(source);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(isBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            httpInterface.requestForObject(requestBuilder.post(RequestBody.create(HttpHelper.JSON_UTF8, resp)).build(), String.class);
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
        if(StringUtils.isBlank(clusters)) {
            return;
        }
        String index = regMap.get(sourceList.get(0).getClass());
        if (StringUtils.isBlank(index)) {
            return;
        }
        StringBuilder urlBuilder = new StringBuilder(50);
        urlBuilder.append(clusters)
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
            // 此处不能使用HttpBasicAuthenticator因为OkHttp的灵活性,它不会一开始就带上Basic验证头,
            // 直到服务器返回401才会去执行authenticator的代码,这时okBuffer已经被读了...
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(isBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            httpInterface.requestForObject(requestBuilder
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
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append(index).append("/")
                .append("_search?type=").append(INDEX_TYPE);
        if (StringUtils.isNotBlank(simpleQuery)) {
            urlBuilder.append("&").append(simpleQuery);
        }
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(isBasicAuth){
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
            if(isBasicAuth){
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
            if(isBasicAuth){
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
            if(isBasicAuth){
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
    public <T> List<T> sqlQueryLog(Class<T> tClass,String sql) {
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append("_sql?_type=").append(INDEX_TYPE);
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(isBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            resp = httpInterface.requestForObject(requestBuilder
                    .post(RequestBody.create(HttpHelper.JSON_UTF8,sql)).build(), String.class);
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
    public <T> SearchResponse<T> sqlQueryLogSearchResponse(Class<T> tClass,String sql) {
        StringBuilder urlBuilder = new StringBuilder(clusters);
        urlBuilder.append("/").append("_sql?_type=").append(INDEX_TYPE);
        String resp = null;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.toString());
            if(isBasicAuth){
                requestBuilder.header("Authorization", Credentials.basic(username, password));
            }
            resp = httpInterface.requestForObject(requestBuilder
                    .post(RequestBody.create(HttpHelper.JSON_UTF8,sql)).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return mapQueryResponseToSearchResponse(resp,tClass);
    }
}
