package dev.kirin.toy.devtools.plugins.mockserver.mock.controller;

import dev.kirin.toy.devtools.plugins.mockserver.annotation.MockController;
import dev.kirin.toy.devtools.plugins.mockserver.mock.facade.MockFacadeV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@MockController
@RequiredArgsConstructor
public class MockControllerV1 {
    private final MockFacadeV1 facade;

    @RequestMapping(value = "/**")
    @ResponseBody
    public ResponseEntity<String> mock(HttpServletRequest httpServletRequest, @RequestBody(required = false) String body) {
        log.info("(mock) requested = {}", httpServletRequest.getRequestURL());
        return facade.mock(httpServletRequest, body);
    }
}
