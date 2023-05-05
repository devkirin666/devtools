package dev.kirin.toy.devtools.plugins.mockserver.handler;

import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.ResponseMatcher;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.RequestContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.RequestContextImpl;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockResponseDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockUrlDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

public interface MockRequestHandler {
    default HandleResult process(HttpServletRequest request, Object body) {
        return process(request, body, null);
    }

    default HandleResult process(HttpServletRequest request, Object body, MockUrlDefinition urlDefinition) {
        return process(RequestContextImpl.of(request, body), urlDefinition);
    }
    HandleResult process(RequestContext requestContext, MockUrlDefinition urlDefinition);

    interface UrlProvider {
        List<? extends MockUrlDefinition> getUrls();
    }

    interface ResponseProvider {
        List<? extends MockResponseDefinition> getResponses(MockUrlDefinition urlDefinition, HttpMethod method);
    }

    interface ErrorProvider {
        MockErrorDefinition getError(MockErrorDefinition.Type type);
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    class HandleResult {
        private boolean success;
        private String uri;
        private HttpStatus status;
        private HttpHeaders headers;
        private String body;
        private HandleDefinition definition;
        private HandleError error;

        public ResponseEntity<String> asResponseEntity() {
            return new ResponseEntity<>(getBody(), getHeaders(), getStatus());
        }
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    class HandleError {
        private Exception exception;
        private MockErrorDefinition.Type type;

        public static HandleError of(MockErrorHandler.ErrorHandleResult error) {
            if(error == null) {
                return null;
            }
            return HandleError.builder()
                    .exception(error.getException())
                    .type(error.getType())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    class HandleDefinition {
        private MockUrlDefinition url;
        private MockResponseDefinition response;
        @Builder.Default
        private List<ResponseMatcher.MatchResult> candidates = Collections.emptyList();
    }
}
