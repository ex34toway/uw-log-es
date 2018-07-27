package uw.log.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.log.es.service.LogService;
import uw.log.es.vo.ESDataList;
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
     * @param logClass 日志类
     */
    public void regLogObject(Class<?> logClass) {
        logService.regLogObject(logClass,null,null);
    }

    /**
     * 注册日志类型
     *
     * @param logClass 日志类
     * @param index    自定义索引名称
     */
    public void regLogObject(Class<?> logClass,String index) {
        logService.regLogObject(logClass,index,null);
    }

    /**
     * 注册日志类型
     *
     * @param logClass 日志类
     * @param index    自定义索引名称
     * @param indexPattern 索引模式
     */
    public void regLogObject(Class<?> logClass,String index,String indexPattern) {
        logService.regLogObject(logClass,index,indexPattern);
    }

    /**
     * 查询日志索引
     *
     * @param logClass
     * @return
     */
    public String getRawIndex(Class<?> logClass) {
        return logService.getRawIndex(logClass);
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
     * 批量写日志
     *
     * @param sourceList 日志对象列表
     * @param <T>
     */
    public <T> void bulkLog(List<T> sourceList) {
        logService.writeBulkLog(sourceList);
    }

    /**
     * 关闭写日志系统
     */
    void destroyLog() {
        logService.destroyLog();
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
        return logService.simpleQueryLog(tClass,logService.getRawIndex(tClass),simpleQuery);
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
        return logService.simpleQueryLogSearchResponse(tClass,logService.getRawIndex(tClass),simpleQuery);
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
        return logService.dslQueryLog(tClass,logService.getRawIndex(tClass),dslQuery);
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
        return logService.dslQueryLogSearchResponse(tClass,logService.getRawIndex(tClass),dslQuery);
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
     * @param startIndex 开始位置
     * @param pageSize 每页记录数
     * @param <T>
     * @return
     */
    public <T> ESDataList<T> sqlQueryLog(Class<T> tClass, String sql, int startIndex, int pageSize) {
        return logService.sqlQueryLog(tClass,sql,startIndex,pageSize);
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
