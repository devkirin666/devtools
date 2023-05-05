package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.expression.EvaluationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Getter
@ToString
public class MockContext {
    private final RequestContext request;
    private final Vars vars;

    @Builder
    public MockContext(RequestContext request) {
        this.request = request;
        this.vars = Vars.generate();
    }

    public static MockContext of(HttpServletRequest request, Map<String, String> pathVariables, Object body) {
        return MockContext.builder()
                .request(RequestContextImpl.of(request, pathVariables, body))
                .build();
    }

    public EvaluationContext asExpressionContext() {
        return new ExpressionContext(this);
    }
}
