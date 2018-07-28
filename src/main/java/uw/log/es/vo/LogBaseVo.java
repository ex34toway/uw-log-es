package uw.log.es.vo;

/**
 * 日志基类Vo
 *
 * @author liliang
 * @since 2018-07-28
 */
public abstract class LogBaseVo {
    /**
     * 应用名称
     */
    protected String appName;
    /**
     * 应用主机地址
     */
    protected String appHost;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppHost() {
        return appHost;
    }

    public void setAppHost(String appHost) {
        this.appHost = appHost;
    }
}
