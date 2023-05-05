package dev.kirin.toy.devtools.plugins.mockserver.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.plugins.mock-server")
@Setter(AccessLevel.MODULE)
@Getter
public class MockServerConfigProperties {
    private int port;
}
