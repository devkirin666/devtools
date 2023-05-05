package dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.dto;

import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.plugins.mockserver.code.ResponseBodyType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.entity.MockErrorEntity;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockErrorDTO implements DTOModel<ErrorType, MockErrorEntity>, MockErrorDefinition {
    private ErrorType type;
    private HttpStatus status;
    private CompiledTemplate headers;
    private ResponseBodyType bodyType;
    private CompiledTemplate body;
    private LocalDateTime lastUpdated;

    @Override
    public ErrorType getId() {
        return getType();
    }

    @Override
    public MockErrorEntity toEntity() {
        return MockErrorEntity.builder()
                .id(getType())
                .status(getStatus())
                .headers(getHeaders())
                .body(getBody())
                .build();
    }

    public static MockErrorDTO fromEntity(MockErrorEntity entity) {
        return MockErrorDTO.builder()
                .type(entity.getId())
                .status(entity.getStatus())
                .headers(entity.getHeaders())
                .body(entity.getBody())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }

    @Override
    public Type getErrorType() {
        return getType().getDefinitionType();
    }
}
