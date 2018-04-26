package zwy.common.log.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zwy.common.log.client.service.LogService;

/**
 * 日志接口服务客户端自动配置类
 *
 * @author Acris,liliang
 * @since 2018/4/24.
 */
@Configuration
@EnableConfigurationProperties({LogClientProperties.class})
public class LogClientAutoConfiguration {

    /**
     * 日志接口服务客户端
     *
     * @param logClientProperties
     * @return
     */
    @Bean
    public LogClient logClient(final LogClientProperties logClientProperties) {
        return new LogClient(new LogService(logClientProperties));
    }
}
