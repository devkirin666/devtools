package dev.kirin.toy.devtools.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Slf4j
@Configuration
class DevelopmentConfig {
    @Bean
    @ConditionalOnProperty(value = "logging.level.web", havingValue = "debug")
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        log.info("(requestLoggingFilter) Enabled request logging.");
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(false);
        filter.setBeforeMessagePrefix("--------------- REQUEST DATA: ");
        filter.setAfterMessagePrefix("--------------- REQUEST DATA: ");
        return filter;
    }
}
