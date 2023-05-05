package dev.kirin.toy.devtools.extras;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springdoc.core.Constants;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;

@Slf4j
class UrlPatternTest {
    @Test
    void test() {
        String uri = "/api/v1/{id}/test";
        String validUri = "/api/v1/123/test";
        String invalidUri = "/api/v1/a/b/test";
        AntPathMatcher pathMatcher = new AntPathMatcher();
        Assertions.assertTrue(pathMatcher.match(uri, validUri));
        Assertions.assertFalse(pathMatcher.match(uri, invalidUri));

        System.out.println(pathMatcher.match(validUri, validUri));
    }

    @Test
    void testCondition() {
        String condition = "body[test] == 1 && 1 == 1";
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("name", "devKirin");
        bodyData.put("test", 1);
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression expression = expressionParser.parseExpression(condition);
        ContextData contextData = ContextData.builder()
                .body(bodyData)
                .build();
        EvaluationContext context = new StandardEvaluationContext(contextData);
        Assertions.assertEquals(Boolean.TRUE, expression.getValue(context, Boolean.class));
    }

    @Test
    void testAntRequestMatcher() {
        String uri = "/docs/api/swagger-ui/index.html";
        String pattern = "/**" + Constants.SWAGGER_UI_PREFIX + "/**" + Constants.INDEX_PAGE;
        AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(pattern);
        log.debug("() pattern = {}", antPathRequestMatcher.getPattern());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        boolean isMatch = antPathRequestMatcher.matches(request);
        log.debug("() isMatch = {}", isMatch);

        AntPathMatcher antPathMatcher = new AntPathMatcher("/");
        antPathMatcher.setTrimTokens(false);
        antPathMatcher.setCaseSensitive(false);
        log.debug("ismatch = {}", antPathMatcher.match(pattern, uri));
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    static class ContextData {
        private Object body;
    }
}
