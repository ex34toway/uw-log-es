package zwy.common.log.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zwy.common.log.client.service.LogService;

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
    public <T> void bulkLog(List<T> sourceList){
        logService.writeBulkLog(sourceList);
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
        return logService.simpleQueryLog(tClass,tClass.getName().toLowerCase(),simpleQuery);
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
        return logService.dslQueryLog(tClass,tClass.getName().toLowerCase(),dslQuery);
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
}
