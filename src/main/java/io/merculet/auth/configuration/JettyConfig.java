package io.merculet.auth.configuration;

import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhou liming
 * @package io.merculet.service
 * @date 2018/8/6 11:10
 * @description
 */
@Configuration
public class JettyConfig {

    @Bean
    public JettyServletWebServerFactory jettyEmbeddedServletContainerFactory(@Value("${server.port}") final String port,
                                                                             @Value("${jetty.threadPool.maxThreads:200}") final String maxThreads,
                                                                             @Value("${jetty.threadPool.minThreads:8}") final String minThreads,
                                                                             @Value("${jetty.threadPool.idleTimeout:60000}") final String idleTimeout) {
        final JettyServletWebServerFactory factory = new JettyServletWebServerFactory(Integer.valueOf(port));
        factory.addServerCustomizers((JettyServerCustomizer) server -> {
            // Tweak the connection pool used by Jetty to handle incoming HTTP connections
            final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
            threadPool.setMaxThreads(Integer.valueOf(maxThreads));
            threadPool.setMinThreads(Integer.valueOf(minThreads));
            threadPool.setIdleTimeout(Integer.valueOf(idleTimeout));

        });
        return factory;
    }
}
