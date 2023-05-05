package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception;

import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import lombok.Getter;

@Getter
public class MockHandleException extends RuntimeException {
    private final MockErrorDefinition.Type type;

    public MockHandleException(String message, MockErrorDefinition.Type type) {
        super(message);
        this.type = type;
    }

    public MockHandleException(String message, Throwable cause, MockErrorDefinition.Type type) {
        super(message, cause);
        this.type = type;
    }

    public MockHandleException(Throwable cause, MockErrorDefinition.Type type) {
        super(cause);
        this.type = type;
    }
}