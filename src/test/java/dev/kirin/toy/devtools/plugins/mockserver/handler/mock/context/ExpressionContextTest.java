package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.Map;

@DisplayName("Customized expression context test")
class ExpressionContextTest {
    private final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    @Test
    @DisplayName("Access map default test")
    void testDefaultMapAccess() {
        Map<String, Object> valueObj = new HashMap<>();
        valueObj.put("value", 1);
        Map<String, Object> rootObj = new HashMap<>();
        rootObj.put("test", valueObj);

        EvaluationContext context = new ExpressionContext(rootObj);
        String accessKeyExpression = "[test][value]";
        Expression expression = EXPRESSION_PARSER.parseExpression(accessKeyExpression);
        Object result = expression.getValue(context);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result);
    }

    @Test
    @DisplayName("Access map as object field test")
    void testAccessMapAsObjectField() {
        Map<String, Object> valueObj = new HashMap<>();
        valueObj.put("value", 1);
        Map<String, Object> rootObj = new HashMap<>();
        rootObj.put("test", valueObj);

        EvaluationContext context = new ExpressionContext(rootObj);
        String accessKeyExpression = "test.value";
        Expression expression = EXPRESSION_PARSER.parseExpression(accessKeyExpression);
        Object result = expression.getValue(context);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result);
    }
}
