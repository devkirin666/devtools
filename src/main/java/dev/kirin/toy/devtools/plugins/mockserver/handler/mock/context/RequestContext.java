package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public interface RequestContext {
    String getUrl();
    String getUri();
    String getMethod();
    HttpHeaders getHeaders();
    Map<String, String> getPathVariables();
    MultiValueMap<String, String> getParams();
    Object getBody();

    void setPathVariables(Map<String, String> pathVariables);
}
