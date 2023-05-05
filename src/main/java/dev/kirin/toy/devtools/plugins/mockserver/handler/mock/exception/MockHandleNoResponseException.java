package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception;

import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.ResponseMatcher;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import lombok.Getter;

import java.util.List;

@Getter
public class MockHandleNoResponseException extends MockHandleException {
    private final List<ResponseMatcher.MatchResult> responses;

    public MockHandleNoResponseException(String message, List<ResponseMatcher.MatchResult> responses) {
        super(message, MockErrorDefinition.Type.NO_RESPONSE);
        this.responses = responses;
    }
}
