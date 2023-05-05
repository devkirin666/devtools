package dev.kirin.toy.devtools.plugins.mockserver.view;

import dev.kirin.common.spring.component.AbstractViewController;
import dev.kirin.toy.devtools.plugins.core.AdminViewController;
import dev.kirin.toy.devtools.plugins.mockserver.config.MockServerPluginInfo;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/view/admin/mock-server")
@RequiredArgsConstructor
public class MockServerManagementViewController extends AbstractViewController implements AdminViewController {
    private final MockServerPluginInfo pluginInfo;

    @GetMapping
    public ModelAndView home() {
        return new ModelAndView("admin/mock-server/index");
    }

    @GetMapping(value = "/urls/{id}")
    public ModelAndView urlDetail(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("admin/mock-server/url-detail");
        modelAndView.addObject("urlId", id);
        return modelAndView;
    }

    @GetMapping(value = "/errors/{type}")
    public ModelAndView errorDetail(@PathVariable("type") ErrorType type) {
        ModelAndView modelAndView = new ModelAndView("admin/mock-server/error-detail");
        modelAndView.addObject("errorType", type);
        return modelAndView;
    }

    @GetMapping(value = "/user-manual")
    public ModelAndView userManual() {
        return new ModelAndView("admin/mock-server/user-manual");
    }

    @ModelAttribute
    void mockServerVariables(Model model) {
        model.addAttribute("mockServer_errorTypes", ErrorType.values());
        model.addAttribute("mockServer_baseUrl", pluginInfo.getManagement().getBaseURL());
    }
}
