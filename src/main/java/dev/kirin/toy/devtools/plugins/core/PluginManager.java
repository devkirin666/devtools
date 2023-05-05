package dev.kirin.toy.devtools.plugins.core;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PluginManager {
    private final Map<String, PluginInfo> pluginMap;

    public PluginManager(List<PluginInfo> plugins) {
        this.pluginMap = plugins.stream()
                .collect(Collectors.toMap(PluginInfo::getName, pluginInfo -> pluginInfo));
    }

    public List<PluginInfo.ManagementInfo> getManagementMenus() {
        return pluginMap.values().stream()
                .sorted(Comparator.comparingInt(o -> o.getManagement().getMenuOrder()))
                .map(PluginInfo::getManagement)
                .collect(Collectors.toList());
    }

    public PluginInfo.ManagementInfo getAnyManagementInfo() {
        return getManagementInfo(null);
    }

    public PluginInfo.ManagementInfo getManagementInfo(String pluginName) {
        if(StringUtils.hasText(pluginName) && pluginMap.containsKey(pluginName)) {
            return pluginMap.get(pluginName).getManagement();
        }
        PluginInfo pluginInfo = pluginMap.values()
                .stream()
                .findAny()
                .orElse(null);
        if(pluginInfo != null) {
            return pluginInfo.getManagement();
        }
        return null;
    }
}
