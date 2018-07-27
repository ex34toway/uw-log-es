package uw.log.es;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uw.log.es.service.LogService;

import javax.annotation.PreDestroy;

/**
 * 日志接口服务客户端自动配置类
 *
 * @author Acris,liliang
 * @since 2018/4/24.
 */
@Configuration
@EnableConfigurationProperties({LogClientProperties.class})
public class LogClientAutoConfiguration {

    private LogClient logClient;

    /**
     * 日志接口服务客户端
     *
     * @param logClientProperties
     * @return
     */
    @Bean
    public LogClient logClient(final LogClientProperties logClientProperties) {
        logClient = new LogClient(new LogService(logClientProperties));
        return logClient;
    }

    @PreDestroy
    public void destroy() {
        logClient.destroyLog();
    }
}
