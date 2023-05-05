package dev.kirin.toy.devtools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {
    @Bean
    PageableArgumentResolver pageableArgumentResolver() {
        return new PageableHandlerMethodArgumentResolver();
    }
}
