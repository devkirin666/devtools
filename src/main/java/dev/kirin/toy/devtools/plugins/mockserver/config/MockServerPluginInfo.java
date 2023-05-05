package dev.kirin.toy.devtools.plugins.mockserver.config;

import dev.kirin.toy.devtools.plugins.core.PluginInfo;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MockServerPluginInfo implements PluginInfo {
    private final String packageName = "dev.kirin.devtools.plugins.mockserver";
    private final String name = "mock-server";
    private final ManagementInfo management = new ManagementInfo();

    @Getter
    public static class ManagementInfo implements PluginInfo.ManagementInfo {
        private final String menuName = "Mock-Server";
        private final String baseURL = "/view/admin/mock-server";
        private final int menuOrder = 0;
    }
}
