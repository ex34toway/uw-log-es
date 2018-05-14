package uw.log.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.log.es.service.LogService;
import uw.log.es.vo.SearchResponse;

import java.util.List;

/**
 * 日志接口服务客户端
 *
 * @author liliang
 * @since 2018-04-25
 */
public class LogClient {

    private static final Logger logger = LoggerFactory.getLogger(LogClient.class);

    private LogService logService;

    public LogClient(final LogService logService) {
        this.logService = logService;
    }

    /**
     * 注册日志类型
     *
     * @param logClass
     */
    public void regLogObject(Class<?> logClass) {
        logService.regLogObject(logClass);
    }

    /**
     * 查询日志索引
     *
     * @param logClass
     * @return
     */
    public String getIndex(Class<?> logClass) {
        return logService.getIndex(logClass);
    }

    /**
     * 写日志
     *
     * @param source 日志对象
     */
    public void log(Object source) {
        logService.writeLog(source);
    }

    /**
     * 写日志
     *
     * @param logClass - 日志类型
     * @param buffer - 日志Buffer
     */
    public void log(Class<?> logClass,okio.Buffer buffer) {
        logService.writeLog(logClass,buffer);
    }

    /**
     * 批量写日志
     *
     * @param sourceList 日志对象列表
     * @param <T>
     */
    public <T> void bulkLog(List<T> sourceList){
        logService.writeBulkLog(sourceList);
    }

    /**
     * 写日志
     *
     * @param logClass - 日志类型
     * @param buffer - 日志Buffer 必须为同一索引
     */
    public void bulkLog(Class<?> logClass,List<okio.Buffer> buffer) {
        logService.writeBulkLog(logClass,buffer);
    }

    /**
     * 简单日志查询
     *
     * @param tClass 日志对象类型
     * @param simpleQuery 简单查询条件
     * @param <T>
     * @return
     */
    public <T> List<T> simpleQueryLog(Class<T> tClass,String simpleQuery) {
        return logService.simpleQueryLog(tClass,logService.getIndex(tClass),simpleQuery);
    }

    /**
     * 简单日志查询
     *
     * @param tClass 日志对象类型
     * @param index 索引
     * @param simpleQuery 简单查询条件
     * @param <T>
     * @return
     */
    public <T> List<T> simpleQueryLog(Class<T> tClass,String index,String simpleQuery) {
        return logService.simpleQueryLog(tClass,index,simpleQuery);
    }

    /**
     * 简单日志查询
     *
     * @param tClass 日志对象类型
     * @param simpleQuery 简单查询条件
     * @param <T>
     * @return
     */
    public <T> SearchResponse<T> simpleQueryLogSearchResponse(Class<T> tClass, String simpleQuery) {
        return logService.simpleQueryLogSearchResponse(tClass,logService.getIndex(tClass),simpleQuery);
    }

    /**
     * 简单日志查询
     *
     * @param tClass 日志对象类型
     * @param index 索引
     * @param simpleQuery 简单查询条件
     * @param <T>
     * @return
     */
    public <T> SearchResponse<T> simpleQueryLogSearchResponse(Class<T> tClass,String index,String simpleQuery) {
        return logService.simpleQueryLogSearchResponse(tClass,index,simpleQuery);
    }

    /**
     * dsl日志查询
     *
     * @param tClass 日志对象类型
     * @param dslQuery dsl查询内容
     * @param <T>
     * @return
     */
    public <T> List<T> dslQueryLog(Class<T> tClass,String dslQuery) {
        return logService.dslQueryLog(tClass,logService.getIndex(tClass),dslQuery);
    }

    /**
     * dsl日志查询
     *
     * @param tClass 日志对象类型
     * @param index 索引
     * @param dslQuery dsl查询内容
     * @param <T>
     * @return
     */
    public <T> List<T> dslQueryLog(Class<T> tClass,String index,String dslQuery) {
        return logService.dslQueryLog(tClass,index,dslQuery);
    }

    /**
     * dsl日志查询
     *
     * @param tClass 日志对象类型
     * @param dslQuery dsl查询内容
     * @param <T>
     * @return
     */
    public <T> SearchResponse<T> dslQueryLogSearchResponse(Class<T> tClass,String dslQuery) {
        return logService.dslQueryLogSearchResponse(tClass,logService.getIndex(tClass),dslQuery);
    }

    /**
     * dsl日志查询
     *
     * @param tClass 日志对象类型
     * @param index 索引
     * @param dslQuery dsl查询内容
     * @param <T>
     * @return
     */
    public <T> SearchResponse<T> dslQueryLogSearchResponse(Class<T> tClass,String index,String dslQuery) {
        return logService.dslQueryLogSearchResponse(tClass,index,dslQuery);
    }

    /**
     * dsl日志查询
     *
     * @param tClass 日志对象类型
     * @param sql sql查询,需要 es 安装 es-sql 插件
     * @param <T>
     * @return
     */
    public <T> List<T> sqlQueryLog(Class<T> tClass,String sql) {
        return logService.sqlQueryLog(tClass,sql);
    }

    /**
     * dsl日志查询
     *
     * @param tClass 日志对象类型
     * @param sql sql查询,需要 es 安装 es-sql 插件
     * @param <T>
     * @return
     */
    public <T> SearchResponse<T> sqlQueryLogSearchResponse(Class<T> tClass,String sql) {
        return logService.sqlQueryLogSearchResponse(tClass,sql);
    }
}
