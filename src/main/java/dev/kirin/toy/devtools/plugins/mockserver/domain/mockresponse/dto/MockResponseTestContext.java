package dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto;

import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.RequestContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockResponseTestContext implements RequestContext {
    private String url;
    private String uri;
    private String method;
    private Map<String, String> pathVariables;
    private HttpHeaders headers;
    private MultiValueMap<String, String> params;
    private Object body;
}
