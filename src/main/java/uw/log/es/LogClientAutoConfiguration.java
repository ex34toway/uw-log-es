package uw.log.es;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uw.auth.client.AuthClientProperties;
import uw.log.es.service.LogService;

import javax.annotation.PreDestroy;
import java.net.InetAddress;

/**
 * 日志接口服务客户端自动配置类
 *
 * @author Acris,liliang
 * @since 2018/4/24.
 */
@Configuration
@EnableConfigurationProperties({LogClientProperties.class})
public class LogClientAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(LogClientAutoConfiguration.class);
    /**
     * 应用名称
     */
    @Value("${spring.application.name}")
    private String appName;
    /**
     * 应用版本
     */
    @Value("${project.version}")
    private String appVersion;
    /**
     * 注册地址
     */
    @Value("${spring.cloud.consul.discovery.ip-address:}")
    private String appHost;

    private LogClient logClient;

    /**
     * 日志接口服务客户端
     *
     * @param logClientProperties
     * @return
     */
    @Bean
    public LogClient logClient(final LogClientProperties logClientProperties,
                               final AuthClientProperties authClientProperties) {
        if (StringUtils.isBlank(appHost)) {
            try {
                InetAddress address = InetAddress.getLocalHost();
                appHost = address.getHostAddress();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        appName = appName + "/" + appVersion;
        appHost = appHost + "/" + authClientProperties.getHostId();
        logClient = new LogClient(new LogService(logClientProperties,appName,appHost));
        return logClient;
    }

    @PreDestroy
    public void destroy() {
        logClient.destroyLog();
    }
}
