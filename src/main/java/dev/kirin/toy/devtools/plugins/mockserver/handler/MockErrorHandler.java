package dev.kirin.toy.devtools.plugins.mockserver.handler;

import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.MockContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception.MockHandleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface MockErrorHandler {
    default ErrorHandleResult handle(MockContext context, MockHandleException e) {
        return handle(context, e.getType(), e);
    }
    ErrorHandleResult handle(MockContext context, MockErrorDefinition.Type type, Exception e);

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    class ErrorHandleResult {
        private MockErrorDefinition.Type type;
        private HttpStatus status;
        private HttpHeaders headers;
        private String body;
        private Exception exception;

        public ResponseEntity<String> asResponseEntity() {
            return new ResponseEntity<>(getBody(), getHeaders(), getStatus());
        }
    }
}
