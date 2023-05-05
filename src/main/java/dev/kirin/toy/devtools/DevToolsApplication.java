package dev.kirin.toy.devtools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"dev.kirin.common.spring", "dev.kirin.toy.devtools"})
public class DevToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevToolsApplication.class, args);
    }

}
