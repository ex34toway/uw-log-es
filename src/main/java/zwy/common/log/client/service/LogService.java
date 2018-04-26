package zwy.common.log.client.service;

import com.google.common.collect.Lists;
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
import zwy.common.log.client.LogClientProperties;
import zwy.common.log.client.vo.SearchResponse;

import java.util.List;

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
     * 写日志
     *
     * @param source 日志对象
     */
    public void writeLog(Object source) {
        StringBuilder urlBuilder = new StringBuilder(logClientProperties.getEsConfig().getClusters());
        urlBuilder.append("/").append(source.getClass().getName().toLowerCase()).append("/")
                .append(INDEX_TYPE);
        try {
            httpInterface.requestForObject(new Request.Builder().url(urlBuilder.toString())
                    .post(RequestBody.create(HttpHelper.JSON_UTF8, ObjectMapper.DEFAULT_JSON_MAPPER.toString(source))).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 批量写日志
     *
     * @param sourceList 日志对象列表
     * @param <T>
     */
    public <T> void writeBulkLog(List<T> sourceList) {
        if(sourceList == null || sourceList.isEmpty()){
            return;
        }
        String index = sourceList.get(0).getClass().getName().toLowerCase();
        StringBuilder urlBuilder = new StringBuilder(logClientProperties.getEsConfig().getClusters());
        urlBuilder.append("/").append("_bulk");
        try {
            StringBuilder bulkBody = new StringBuilder();
            for(T source : sourceList){
                bulkBody.append("{ \"index\": { \"_index\": \"").append(index)
                        .append("\", \"_type\": \"").append(INDEX_TYPE)
                        .append("\"}}").append("\n");
                bulkBody.append(ObjectMapper.DEFAULT_JSON_MAPPER.toString(source)).append("\n");
            }
            httpInterface.requestForObject(new Request.Builder().url(urlBuilder.toString())
                    .post(RequestBody.create(HttpHelper.JSON_UTF8, bulkBody.toString())).build(), String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
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
