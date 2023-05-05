package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context;

import dev.kirin.common.core.model.Tuple;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@ToString
public class RequestContextImpl implements RequestContext{
    private final String url;
    private final String uri;
    private final String method;
    private final HttpHeaders headers;
    
    @Setter
    private Map<String, String> pathVariables;
    private final MultiValueMap<String, String> params;
    private final Object body;

    @Builder(access = AccessLevel.PRIVATE)
    public RequestContextImpl(HttpServletRequest request, Map<String, String> pathVariables, Object body) {
        this.url = request.getRequestURL().toString();
        this.uri = request.getRequestURI();
        this.method = request.getMethod();
        this.headers = asHeaders(request);
        this.pathVariables = pathVariables;
        this.params = asParams(request);
        this.body = body;
    }

    private HttpHeaders asHeaders(HttpServletRequest request) {
        Map<String, List<String>> headerMap = Collections.list(request.getHeaderNames())
                .stream()
                .map(name -> new Tuple<>(name, Collections.list(request.getHeaders(name))))
                .collect(Collectors.toMap(Tuple::getKey, Tuple::getValue));
        return new HttpHeaders(new LinkedMultiValueMap<>(headerMap));
    }

    private MultiValueMap<String, String> asParams(HttpServletRequest request) {
        Map<String, List<String>> parameterMap = request.getParameterMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.asList(entry.getValue())));
        return new LinkedMultiValueMap<>(parameterMap);
    }

    public static RequestContextImpl of(HttpServletRequest request, Object body) {
        return of(request, null, body);
    }
    
    public static RequestContextImpl of(HttpServletRequest request, Map<String, String> pathVariables, Object body) {
        return RequestContextImpl.builder()
                .request(request)
                .pathVariables(pathVariables)
                .body(body)
                .build();
    }
}
