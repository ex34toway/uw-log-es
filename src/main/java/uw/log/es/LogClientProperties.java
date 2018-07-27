package uw.log.es;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 日志接口服务客户端属性配置类
 *
 * @author Acris,liliang
 * @since 2018/4/24.
 */
@ConfigurationProperties(prefix = "uw.log")
public class LogClientProperties {

    private EsConfig es = new EsConfig();

    /**
     * ES主机配置
     */
    public static class EsConfig {

        /**
         * 连接超时
         */
        private long connectTimeout = 10000;

        /**
         * 读超时
         */
        private long readTimeout = 10000;

        /**
         * 写超时
         */
        private long writeTimeout = 10000;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * ES集群HTTP REST地址
         */
        private String clusters = null;

        /**
         * Elasticsearch bulk api 地址
         */
        private String esBulk = "/_bulk";

        /**
         * READ_ONLY: 只读模式; READ_WRITE: 读写模式[会有后台线程开销]
         */
        private LogMode mode = LogMode.READ_WRITE;

        /**
         * 刷新Bucket时间毫秒数
         */
        private long maxFlushInMilliseconds = 10000;

        /**
         * 允许最大Bucket 字节数。
         */
        private long maxBytesOfBatch = 5*1024*1024;

        /**
         * 最大批量线程数。
         */
        private int maxBatchThreads = 3;

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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getClusters() {
            return clusters;
        }

        public void setClusters(String clusters) {
            this.clusters = clusters;
        }

        public String getEsBulk() {
            return esBulk;
        }

        public void setEsBulk(String esBulk) {
            this.esBulk = esBulk;
        }

        public LogMode getMode() {
            return mode;
        }

        public void setMode(LogMode mode) {
            this.mode = mode;
        }

        public long getMaxFlushInMilliseconds() {
            return maxFlushInMilliseconds;
        }

        public void setMaxFlushInMilliseconds(long maxFlushInMilliseconds) {
            this.maxFlushInMilliseconds = maxFlushInMilliseconds;
        }

        public long getMaxBytesOfBatch() {
            return maxBytesOfBatch;
        }

        public void setMaxBytesOfBatch(long maxBytesOfBatch) {
            this.maxBytesOfBatch = maxBytesOfBatch;
        }

        public int getMaxBatchThreads() {
            return maxBatchThreads;
        }

        public void setMaxBatchThreads(int maxBatchThreads) {
            this.maxBatchThreads = maxBatchThreads;
        }
    }

    public enum LogMode {
        READ_ONLY, READ_WRITE
    }

    public EsConfig getEs() {
        return es;
    }

    public void setEs(EsConfig es) {
        this.es = es;
    }
}
