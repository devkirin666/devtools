package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception;

import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockUrlDefinition;
import lombok.Getter;

import java.util.List;

@Getter
public class MockHandleDuplicateUrlsException extends MockHandleException {
    private final List<MockUrlDefinition> duplicateUrls;

    public MockHandleDuplicateUrlsException(String message, List<MockUrlDefinition> duplicateUrls) {
        super(message, MockErrorDefinition.Type.MULTI_URL);
        this.duplicateUrls = duplicateUrls;
    }
}