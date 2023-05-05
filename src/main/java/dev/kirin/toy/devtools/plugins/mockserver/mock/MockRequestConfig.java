package dev.kirin.toy.devtools.plugins.mockserver.mock;

import dev.kirin.toy.devtools.config.security.PortMatcher;
import dev.kirin.toy.devtools.plugins.mockserver.config.MockServerConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Slf4j
@Configuration
@ConditionalOnBean(MockServerConfigProperties.class)
@RequiredArgsConstructor
public class MockRequestConfig {
    private final MockServerConfigProperties properties;

    @Bean
    public TomcatServletWebServerFactory mockServletContainer() {
        log.info("(mockServletContainer) mock server port = {}", properties.getPort());

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(properties.getPort());

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }

    @Bean
    public WebMvcRegistrations webMvcRegistrationsHandlerMapping() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new PortBasedRequestMappingHandlerMapping(properties);
            }
        };
    }

    @Bean
    SecurityFilterChain mockUnsecureFilterChain(HttpSecurity http) throws Exception {
        return http.requestMatcher(new PortMatcher(properties.getPort()))
                .antMatcher("/**")
                .anonymous()
                .and()
                    .csrf().disable()
                    .cors().disable()
                    .formLogin().disable()
                .build();
    }
}
