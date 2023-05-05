package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition;

import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.plugins.mockserver.code.ResponseBodyType;
import org.springframework.http.HttpStatus;

public interface MockErrorDefinition {
    HttpStatus getStatus();
    Type getErrorType();
    CompiledTemplate getHeaders();
    ResponseBodyType getBodyType();
    CompiledTemplate getBody();

    enum Type {
        NOT_FOUND, NO_RESPONSE, MULTI_URL, UNKNOWN
    }
}
