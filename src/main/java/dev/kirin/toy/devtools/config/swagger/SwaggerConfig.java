package dev.kirin.toy.devtools.config.swagger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "springdoc.swagger-ui.enabled", havingValue = "true")
public class SwaggerConfig {
}
