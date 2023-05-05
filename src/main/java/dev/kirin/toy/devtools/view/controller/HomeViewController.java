package dev.kirin.toy.devtools.view.controller;

import dev.kirin.common.spring.component.AbstractViewController;
import dev.kirin.toy.devtools.plugins.core.AdminViewController;
import dev.kirin.toy.devtools.plugins.core.PluginManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(value = {"/", "/admin"})
@RequiredArgsConstructor
public class HomeViewController extends AbstractViewController implements AdminViewController {
    private final PluginManager pluginManager;

    @GetMapping
    public RedirectView home() {
        return new RedirectView(pluginManager.getAnyManagementInfo().getBaseURL());
    }

}
