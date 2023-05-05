package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseTestContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockRequestHandler;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.ResponseMatcher;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockResponseDefinition;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockUrlTestVoV1 {
    @Data
    @NoArgsConstructor
    public static class Request {
        private boolean saveHistory;
        @NotEmpty
        private String uri;
        @NotNull
        private HttpMethod method;
        private LinkedMultiValueMap<String, String> params;
        private HttpHeaders headers;
        private String body;

        public MockResponseTestContext asContextData() {
            return MockResponseTestContext.builder()
                    .uri(getUri())
                    .method(getMethod().name())
                    .params(getParams())
                    .headers(getHeaders())
                    .body(getBody())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private boolean success;
        private String id;
        private String name;
        private String uri;
        private int status;
        private HttpHeaders headers;
        private String body;
        private List<MockResponseDetail> responses;
        private String errorType;

        public static Response of(String id, String name, MockRequestHandler.HandleResult handleResult) {
            String errorType = null;
            if(!handleResult.isSuccess()) {
                errorType = ErrorType.findBy(handleResult.getError().getType()).getTitle();
            }

            String selectedId = handleResult.getDefinition().getResponse() == null ?
                    StringUtil.BLANK : handleResult.getDefinition().getResponse().getId();

            List<MockResponseDetail> responses = handleResult.getDefinition().getCandidates()
                    .stream()
                    .map(MockResponseDetail::of)
                    .peek(detail -> detail.setSelected(detail.getId().equals(selectedId)))
                    .collect(Collectors.toList());
            return Response.builder()
                    .success(handleResult.isSuccess())
                    .id(id)
                    .name(name)
                    .uri(handleResult.getUri())
                    .status(handleResult.getStatus().value())
                    .headers(handleResult.getHeaders())
                    .body(handleResult.getBody())
                    .responses(responses)
                    .errorType(errorType)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MockResponseDetail {
        private boolean selected;
        private String id;
        private String condition;
        private int no;
        private String description;
        private boolean matched;
        private String cause;

        public static MockResponseDetail of(ResponseMatcher.MatchResult matchResult) {
            if(matchResult == null) {
                return null;
            }
            String cause = StringUtil.BLANK;
            Exception exception = matchResult.getException();
            if(exception != null) {
                cause = exception.getLocalizedMessage();
            }
            MockResponseDefinition definition = matchResult.getDefinition();
            return MockResponseDetail.builder()
                    .id(definition.getId())
                    .condition(definition.getCondition())
                    .no(definition.getOrder())
                    .description(definition.getDescription())
                    .matched(matchResult.isMatched())
                    .cause(cause)
                    .build();
        }
    }
}
