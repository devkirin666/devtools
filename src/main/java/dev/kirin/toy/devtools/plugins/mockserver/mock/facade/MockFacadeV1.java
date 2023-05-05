package dev.kirin.toy.devtools.plugins.mockserver.mock.facade;

import dev.kirin.toy.devtools.plugins.mockserver.handler.MockRequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockFacadeV1 {
    private final MockRequestHandler mockRequestHandler;

    public ResponseEntity<String> mock(HttpServletRequest httpServletRequest, String body) {
        MockRequestHandler.HandleResult handleResult = mockRequestHandler.process(httpServletRequest, body);
        return handleResult.asResponseEntity();
    }
}
