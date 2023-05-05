package dev.kirin.toy.devtools.plugins.mockserver.handler.mock;

import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.MockContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockResponseDefinition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ResponseMatcher {
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    List<MatchResult> match(MockContext context, List<? extends MockResponseDefinition> responseDefinitions) {
        EvaluationContext expressionContext = context.asExpressionContext();
        return responseDefinitions.stream()
                .map(response -> {
                        Exception exception = null;
                        boolean isMatched = false;
                        try {
                            isMatched = isMatchResponseCondition(expressionContext, response);
                        } catch (Exception e) {
                            log.error("(process) invalid condition. cause = {}, condition = {}", e.getLocalizedMessage(), response.getCondition());
                            exception = e;
                        }
                        return MatchResult.builder()
                                .matched(isMatched)
                                .exception(exception)
                                .definition(response)
                                .build();
                })
                .sorted()
                .collect(Collectors.toList());
    }

    private boolean isMatchResponseCondition(EvaluationContext context, MockResponseDefinition responseDefinition) {
        Expression expression = EXPRESSION_PARSER.parseExpression(responseDefinition.getCondition());
        log.debug("(isMatchResponseCondition) expressionValue = {}", expression.getValue(context));
        Boolean expressionValue = expression.getValue(context, Boolean.class);
        if(expressionValue == null) {
            throw new NullPointerException("'condition' result is null.");
        }
        return Boolean.TRUE.equals(expressionValue);
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MatchResult implements Comparable<MatchResult> {
        private boolean matched;
        private Exception exception;
        private MockResponseDefinition definition;

        @Override
        public int compareTo(MatchResult o) {
            return getDefinition().compareTo(o.getDefinition());
        }
    }
}
