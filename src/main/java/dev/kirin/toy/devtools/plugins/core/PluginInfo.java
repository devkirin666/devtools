package dev.kirin.toy.devtools.plugins.core;

public interface PluginInfo {
    String getPackageName();
    String getName();
    ManagementInfo getManagement();

    interface ManagementInfo {
        String getBaseURL();
        String getMenuName();
        int getMenuOrder();
    }
}
