package dev.kirin.toy.devtools.view.advisor;

import dev.kirin.toy.devtools.plugins.core.AdminViewController;
import dev.kirin.toy.devtools.plugins.core.PluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice(assignableTypes = {AdminViewController.class})
@RequiredArgsConstructor
public class AdminViewAdvisor {
    private final PluginManager pluginManager;

    @ModelAttribute
    void globalVariables(Model model) {
        model.addAttribute("global_plugin_menus", pluginManager.getManagementMenus());
    }
}
