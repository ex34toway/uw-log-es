package zwy.common.log.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 日志接口服务客户端属性配置类
 *
 * @author Acris,liliang
 * @since 2018/4/24.
 */
@ConfigurationProperties(prefix = "zwy.common.log.client")
public class LogClientProperties {

    private EsConfig esConfig = new EsConfig();

    /**
     * ES主机配置
     */
    public static class EsConfig {

        private long connectTimeout = 10000;

        private long readTimeout = 10000;

        private long writeTimeout = 10000;

        private String clusters = "http://localhost:9200";

        public long getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public long getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
        }

        public long getWriteTimeout() {
            return writeTimeout;
        }

        public void setWriteTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
        }

        public String getClusters() {
            return clusters;
        }

        public void setClusters(String clusters) {
            this.clusters = clusters;
        }
    }

    public EsConfig getEsConfig() {
        return esConfig;
    }

    public void setEsConfig(EsConfig esConfig) {
        this.esConfig = esConfig;
    }
}
