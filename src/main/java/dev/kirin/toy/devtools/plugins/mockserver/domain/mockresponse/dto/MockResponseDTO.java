package dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto;

import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.plugins.mockserver.code.ResponseBodyType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.entity.MockResponseEntity;
import dev.kirin.toy.devtools.plugins.mockserver.exception.InvalidResponseConditionException;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockResponseDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Slf4j
public class MockResponseDTO implements DTOModel<String, MockResponseEntity>, MockResponseDefinition {
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    private String id;
    private String mockUrlId;

    private Integer no;
    private HttpMethod method;
    private String condition;

    private HttpStatus status;
    private ResponseBodyType bodyType;
    private CompiledTemplate body;
    private CompiledTemplate headers;

    private String description;
    private Boolean enabled;
    private LocalDateTime lastUpdated;

    public boolean isMatchCondition(EvaluationContext context) {
        Expression expression = EXPRESSION_PARSER.parseExpression(getCondition());
        try {
            Boolean expressionValue = expression.getValue(context, Boolean.class);
            log.debug("(isMatchCondition) value = {}", expressionValue);
            return Boolean.TRUE.equals(expressionValue);
        } catch (Exception e) {
            throw new InvalidResponseConditionException("Cannot check condition. condition = " + getCondition(), e);
        }
    }

    public static EvaluationContext buildContext(MockResponseTestContext data) {
        return new StandardEvaluationContext(data);
    }

    @Override
    public MockResponseEntity toEntity() {
        return MockResponseEntity.builder()
                .id(getId())
                .mockUrlId(getMockUrlId())
                .no(getNo())
                .method(getMethod())
                .condition(getCondition())
                .status(getStatus() == null ? null : getStatus().value())
                .body(getBody())
                .bodyType(getBodyType())
                .headers(getHeaders())
                .enabled(getEnabled())
                .description(getDescription())
                .build();
    }

    public static MockResponseDTO fromEntity(MockResponseEntity entity) {
        if(entity == null) {
            return null;
        }
        return MockResponseDTO.builder()
                .id(entity.getId())
                .mockUrlId(entity.getMockUrlId())
                .no(entity.getNo())
                .method(entity.getMethod())
                .condition(entity.getCondition())
                .status(HttpStatus.resolve(entity.getStatus()))
                .bodyType(entity.getBodyType())
                .body(entity.getBody())
                .headers(entity.getHeaders())
                .enabled(entity.getEnabled())
                .description(entity.getDescription())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }

    @Override
    public int getOrder() {
        return getNo();
    }
}
