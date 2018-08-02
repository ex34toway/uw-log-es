package uw.log.es.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 日志基类Vo
 *
 * @author liliang
 * @since 2018-07-28
 */
public abstract class LogBaseVo {
    /**
     * 记录时间,方便kibana分析
     */
    @JsonProperty("@timestamp")
    protected String timestamp;
    /**
     * 应用名称
     */
    protected String appName;
    /**
     * 应用主机地址
     */
    protected String appHost;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

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
