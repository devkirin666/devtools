package dev.kirin.toy.devtools.plugins.mockserver.exception;

public class InvalidResponseConditionException extends RuntimeException {
    public InvalidResponseConditionException(String message) {
        super(message);
    }

    public InvalidResponseConditionException(String message, Throwable cause) {
        super(message, cause);
    }
}
