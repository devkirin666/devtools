package dev.kirin.toy.devtools.plugins.mockserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.junit.answer.GenericAnswer;
import dev.kirin.common.template.Template;
import dev.kirin.common.template.TemplateAutoConfigure;
import dev.kirin.common.template.compile.TemplateParseContext;
import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.config.JsonConfig;
import dev.kirin.toy.devtools.plugins.mockserver.code.ResponseBodyType;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.MockErrorHandlerImpl;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.MockRequestHandlerImpl;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.ResponseMatcher;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockResponseDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockUrlDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception.MockHandleDuplicateUrlsException;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception.MockHandleException;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception.MockHandleNoResponseException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@Slf4j
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {JsonConfig.class, TemplateAutoConfigure.class})
class MockRequestHandlerTests {
    private final MockUrlDefinition EXPECT_URL_DEFINITION = new TestMockUrlDefinition("expect-url-id", "/api/test");

    private final ObjectMapper objectMapper = JsonConfig.OBJECT_MAPPER;

    @Autowired
    private Template template;

    private final MockRequestHandler.UrlProvider urlProvider = Mockito.spy(MockRequestHandler.UrlProvider.class);
    private final MockRequestHandler.ResponseProvider responseProvider = Mockito.spy(MockRequestHandler.ResponseProvider.class);
    private final MockRequestHandler.ErrorProvider errorProvider = Mockito.spy(MockRequestHandler.ErrorProvider.class);
    private MockErrorHandler errorHandler;

    private MockRequestHandler handler;

    private TemplateParseContext parserContext;

    @BeforeEach
    void beforeEach() {
        this.parserContext = template.makeParseContext();
        this.errorHandler = new MockErrorHandlerImpl(errorProvider, template, objectMapper);
        this.handler =  new MockRequestHandlerImpl(urlProvider, responseProvider, this.errorHandler, template, objectMapper);
    }

    @Test
    @DisplayName("[Fail] Not found url")
    void failNotfoundUrl() {
        MockErrorDefinition.Type errorType = MockErrorDefinition.Type.NOT_FOUND;
        HttpStatus errorResponseStatus = HttpStatus.NOT_FOUND;

        TestMockErrorDefinition errorDefinition = TestMockErrorDefinition.builder()
                .errorType(errorType)
                .status(errorResponseStatus)
                .headers(emptyCompiledTemplate())
                .bodyType(ResponseBodyType.TEXT)
                .body(emptyCompiledTemplate())
                .build();

        Mockito.when(errorProvider.getError(errorType))
                .thenReturn(errorDefinition);

        String uri = "/api/not-found";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        MockRequestHandler.HandleResult handleResult = handler.process(request, "");
        Assertions.assertFalse(handleResult.isSuccess());
        Assertions.assertEquals(uri, handleResult.getUri());
        Assertions.assertEquals(errorDefinition.getStatus(), handleResult.getStatus());
        Assertions.assertNull(handleResult.getDefinition());
        Assertions.assertNotNull(handleResult.getHeaders());
        Assertions.assertNotNull(handleResult.getBody());

        MockRequestHandler.HandleError resultError = handleResult.getError();
        Assertions.assertNotNull(resultError);
        Assertions.assertEquals(errorType, resultError.getType());

        Exception exception = resultError.getException();
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MockHandleException.class, exception.getClass());
    }

    @Test
    @DisplayName("[Fail] Multiple URLs - Duplicated URI values")
    void failMultipleUrls_whenDuplicatedUriValues() {
        List<TestMockUrlDefinition> multipleUrlDefinitions = Arrays.asList(
                new TestMockUrlDefinition("multiple-url-id-1", "/api/multi/test")
                , new TestMockUrlDefinition("multiple-url-id-2", "/api/multi/test")
        );
        Mockito.when(urlProvider.getUrls())
                .thenAnswer(new GenericAnswer<>(multipleUrlDefinitions));

        MockErrorDefinition.Type errorType = MockErrorDefinition.Type.MULTI_URL;
        HttpStatus errorResponseStatus = HttpStatus.MULTIPLE_CHOICES;

        TestMockErrorDefinition errorDefinition = TestMockErrorDefinition.builder()
                .errorType(errorType)
                .status(errorResponseStatus)
                .headers(emptyCompiledTemplate())
                .bodyType(ResponseBodyType.TEXT)
                .body(emptyCompiledTemplate())
                .build();

        Mockito.when(errorProvider.getError(errorType))
                .thenReturn(errorDefinition);

        String uri = "/api/multi/test";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        MockRequestHandler.HandleResult handleResult = handler.process(request, "");
        Assertions.assertFalse(handleResult.isSuccess());
        Assertions.assertEquals(uri, handleResult.getUri());
        Assertions.assertEquals(errorDefinition.getStatus(), handleResult.getStatus());
        Assertions.assertNull(handleResult.getDefinition());
        Assertions.assertNotNull(handleResult.getHeaders());
        Assertions.assertNotNull(handleResult.getBody());

        MockRequestHandler.HandleError resultError = handleResult.getError();
        Assertions.assertNotNull(resultError);
        Assertions.assertEquals(errorType, resultError.getType());

        Exception exception = resultError.getException();
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MockHandleDuplicateUrlsException.class, exception.getClass());

        MockHandleDuplicateUrlsException duplicateUrlsException = (MockHandleDuplicateUrlsException) exception;
        List<MockUrlDefinition> duplicateUrls = duplicateUrlsException.getDuplicateUrls();
        Assertions.assertNotNull(duplicateUrls);
        Assertions.assertEquals(multipleUrlDefinitions, duplicateUrls);
    }

    @Test
    @DisplayName("[Fail] Multiple URLs - Duplicated Path variables")
    void failMultipleUrls_whenPathVariablesDuplicated() {
        List<TestMockUrlDefinition> multipleUrlDefinitions = Arrays.asList(
                new TestMockUrlDefinition("multiple-url-id-1", "/api/{var1}/test")
                , new TestMockUrlDefinition("multiple-url-id-2", "/api/{var2}/test")
        );
        Mockito.when(urlProvider.getUrls())
                .thenAnswer(new GenericAnswer<>(multipleUrlDefinitions));

        MockErrorDefinition.Type errorType = MockErrorDefinition.Type.MULTI_URL;
        HttpStatus errorResponseStatus = HttpStatus.MULTIPLE_CHOICES;

        TestMockErrorDefinition errorDefinition = TestMockErrorDefinition.builder()
                .errorType(errorType)
                .status(errorResponseStatus)
                .headers(emptyCompiledTemplate())
                .bodyType(ResponseBodyType.TEXT)
                .body(emptyCompiledTemplate())
                .build();

        Mockito.when(errorProvider.getError(errorType))
                .thenReturn(errorDefinition);

        String uri = "/api/multi/test";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        MockRequestHandler.HandleResult handleResult = handler.process(request, "");
        Assertions.assertFalse(handleResult.isSuccess());
        Assertions.assertEquals(uri, handleResult.getUri());
        Assertions.assertEquals(errorDefinition.getStatus(), handleResult.getStatus());
        Assertions.assertNull(handleResult.getDefinition());
        Assertions.assertNotNull(handleResult.getHeaders());
        Assertions.assertNotNull(handleResult.getBody());

        MockRequestHandler.HandleError resultError = handleResult.getError();
        Assertions.assertNotNull(resultError);
        Assertions.assertEquals(errorType, resultError.getType());

        Exception exception = resultError.getException();
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MockHandleDuplicateUrlsException.class, exception.getClass());

        MockHandleDuplicateUrlsException duplicateUrlsException = (MockHandleDuplicateUrlsException) exception;
        List<MockUrlDefinition> duplicateUrls = duplicateUrlsException.getDuplicateUrls();
        Assertions.assertNotNull(duplicateUrls);
        Assertions.assertEquals(multipleUrlDefinitions, duplicateUrls);
    }

    @Test
    @DisplayName("[OK] Invalid condition. Result is null.")
    void failUnknown_whenInvalidConditionResultIsNull() {
        TestMockUrlDefinition urlDefinition = new TestMockUrlDefinition("unknown-url-id-1", "/api/unknown");
        List<TestMockUrlDefinition> multipleUrlDefinitions = Collections.singletonList(urlDefinition);
        Mockito.when(urlProvider.getUrls())
                .thenAnswer(new GenericAnswer<>(multipleUrlDefinitions));

        TestMockResponseDefinition invalidJsonFormatResponse = TestMockResponseDefinition.builder()
                .status(HttpStatus.OK)
                .headers(emptyCompiledTemplate())
                .method(HttpMethod.POST)
                .condition("request.method")
                .order(0)
                .bodyType(ResponseBodyType.JSON)
                .body(template.compile(parserContext, "{aaa:"))
                .build();
        List<TestMockResponseDefinition> responses = Collections.singletonList(invalidJsonFormatResponse);
        Mockito.when(responseProvider.getResponses(urlDefinition, invalidJsonFormatResponse.getMethod()))
                .thenAnswer(new GenericAnswer<>(responses));

        MockErrorDefinition.Type errorType = MockErrorDefinition.Type.NO_RESPONSE;
        HttpStatus errorResponseStatus = HttpStatus.NO_CONTENT;

        TestMockErrorDefinition errorDefinition = TestMockErrorDefinition.builder()
                .errorType(errorType)
                .status(errorResponseStatus)
                .headers(emptyCompiledTemplate())
                .bodyType(ResponseBodyType.TEXT)
                .body(emptyCompiledTemplate())
                .build();

        Mockito.when(errorProvider.getError(errorType))
                .thenReturn(errorDefinition);

        String uri = "/api/unknown";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        request.setMethod(invalidJsonFormatResponse.getMethod().name());

        MockRequestHandler.HandleResult handleResult = handler.process(request, "");
        Assertions.assertFalse(handleResult.isSuccess());
        Assertions.assertEquals(uri, handleResult.getUri());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, handleResult.getStatus());
        Assertions.assertEquals(0, handleResult.getHeaders().size());
        Assertions.assertEquals(StringUtil.BLANK, handleResult.getBody());

        MockRequestHandler.HandleError error = handleResult.getError();
        Assertions.assertNotNull(error);
        Assertions.assertEquals(errorType, error.getType());
        Assertions.assertEquals(MockHandleNoResponseException.class, error.getException().getClass());

        MockRequestHandler.HandleDefinition definition = handleResult.getDefinition();
        Assertions.assertNotNull(definition);

        MockUrlDefinition url = definition.getUrl();
        Assertions.assertNotNull(url);
        Assertions.assertEquals(urlDefinition.getId(), url.getId());
        Assertions.assertEquals(urlDefinition.getUri(), url.getUri());

        Assertions.assertNull(definition.getResponse());

        List<ResponseMatcher.MatchResult> candidates = definition.getCandidates();
        Assertions.assertNotNull(candidates);
        Assertions.assertEquals(1, candidates.size());

        ResponseMatcher.MatchResult responseMatchResult = candidates.get(0);
        Assertions.assertFalse(responseMatchResult.isMatched());

        Exception exception = responseMatchResult.getException();
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(SpelEvaluationException.class, exception.getClass());

        MockResponseDefinition definition1 = responseMatchResult.getDefinition();
        Assertions.assertNotNull(definition1);
        Assertions.assertEquals(invalidJsonFormatResponse.getId(), definition1.getId());
        Assertions.assertEquals(invalidJsonFormatResponse.getCondition(), definition1.getCondition());
        Assertions.assertEquals(invalidJsonFormatResponse.getOrder(), definition1.getOrder());
    }

    @Test
    @DisplayName("[OK] Invalid condition. Result is not boolean")
    void testInvalidConditionResultIsNotBoolean() {
        TestMockUrlDefinition urlDefinition = new TestMockUrlDefinition("unknown-url-id-1", "/api/unknown");
        List<TestMockUrlDefinition> multipleUrlDefinitions = Collections.singletonList(urlDefinition);
        Mockito.when(urlProvider.getUrls())
                .thenAnswer(new GenericAnswer<>(multipleUrlDefinitions));

        TestMockResponseDefinition invalidJsonFormatResponse = TestMockResponseDefinition.builder()
                .status(HttpStatus.OK)
                .headers(emptyCompiledTemplate())
                .method(HttpMethod.POST)
                .condition("request.method")
                .order(0)
                .bodyType(ResponseBodyType.JSON)
                .body(emptyCompiledTemplate())
                .build();
        List<TestMockResponseDefinition> responses = Collections.singletonList(invalidJsonFormatResponse);
        Mockito.when(responseProvider.getResponses(urlDefinition, invalidJsonFormatResponse.getMethod()))
                .thenAnswer(new GenericAnswer<>(responses));

        MockErrorDefinition.Type errorType = MockErrorDefinition.Type.NO_RESPONSE;
        HttpStatus errorResponseStatus = HttpStatus.NO_CONTENT;

        TestMockErrorDefinition errorDefinition = TestMockErrorDefinition.builder()
                .errorType(errorType)
                .status(errorResponseStatus)
                .headers(emptyCompiledTemplate())
                .bodyType(ResponseBodyType.TEXT)
                .body(emptyCompiledTemplate())
                .build();

        Mockito.when(errorProvider.getError(errorType))
                .thenReturn(errorDefinition);

        String uri = "/api/unknown";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.name());
        request.setRequestURI(uri);

        MockRequestHandler.HandleResult handleResult = handler.process(request, "");
        Assertions.assertFalse(handleResult.isSuccess());
        Assertions.assertEquals(uri, handleResult.getUri());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, handleResult.getStatus());
        Assertions.assertEquals(0, handleResult.getHeaders().size());
        Assertions.assertEquals(StringUtil.BLANK, handleResult.getBody());

        MockRequestHandler.HandleError error = handleResult.getError();
        Assertions.assertNotNull(error);
        Assertions.assertEquals(errorType, error.getType());
        Assertions.assertEquals(MockHandleNoResponseException.class, error.getException().getClass());

        MockRequestHandler.HandleDefinition definition = handleResult.getDefinition();
        Assertions.assertNotNull(definition);

        MockUrlDefinition url = definition.getUrl();
        Assertions.assertNotNull(url);
        Assertions.assertEquals(urlDefinition.getId(), url.getId());
        Assertions.assertEquals(urlDefinition.getUri(), url.getUri());

        Assertions.assertNull(definition.getResponse());

        List<ResponseMatcher.MatchResult> candidates = definition.getCandidates();
        Assertions.assertNotNull(candidates);
        Assertions.assertEquals(1, candidates.size());

        ResponseMatcher.MatchResult responseMatchResult = candidates.get(0);
        Assertions.assertFalse(responseMatchResult.isMatched());

        Exception exception = responseMatchResult.getException();
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(SpelEvaluationException.class, exception.getClass());

        MockResponseDefinition definition1 = responseMatchResult.getDefinition();
        Assertions.assertNotNull(definition1);
        Assertions.assertEquals(invalidJsonFormatResponse.getId(), definition1.getId());
        Assertions.assertEquals(invalidJsonFormatResponse.getCondition(), definition1.getCondition());
        Assertions.assertEquals(invalidJsonFormatResponse.getOrder(), definition1.getOrder());
    }


    @Test
    @DisplayName("[OK] Defined error response")
    void testDefinedErrorResponse() {
        TestMockUrlDefinition urlDefinition = new TestMockUrlDefinition("unknown-url-id-1", "/api/unknown");
        List<TestMockUrlDefinition> multipleUrlDefinitions = Collections.singletonList(urlDefinition);
        Mockito.when(urlProvider.getUrls())
                .thenAnswer(new GenericAnswer<>(multipleUrlDefinitions));

        TestMockResponseDefinition invalidJsonFormatResponse = TestMockResponseDefinition.builder()
                .status(HttpStatus.OK)
                .headers(emptyCompiledTemplate())
                .condition("request.method")
                .method(HttpMethod.POST)
                .order(0)
                .bodyType(ResponseBodyType.JSON)
                .body(template.compile(parserContext, "{\"\":"))
                .build();
        List<TestMockResponseDefinition> responses = Collections.singletonList(invalidJsonFormatResponse);
        Mockito.when(responseProvider.getResponses(urlDefinition, invalidJsonFormatResponse.getMethod()))
                .thenAnswer(new GenericAnswer<>(responses));

        MockErrorDefinition.Type errorType = MockErrorDefinition.Type.UNKNOWN;
        HttpStatus errorResponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        TestMockErrorDefinition errorDefinition = TestMockErrorDefinition.builder()
                .errorType(errorType)
                .status(errorResponseStatus)
                .headers(emptyCompiledTemplate())
                .bodyType(ResponseBodyType.TEXT)
                .body(emptyCompiledTemplate())
                .build();

        Mockito.when(errorProvider.getError(errorType))
                .thenReturn(errorDefinition);

        String uri = "/api/unknown";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        MockRequestHandler.HandleResult handleResult = handler.process(request, "");
        Assertions.assertFalse(handleResult.isSuccess());
        Assertions.assertEquals(uri, handleResult.getUri());
        Assertions.assertEquals(errorDefinition.getStatus(), handleResult.getStatus());
        Assertions.assertNotNull(handleResult.getDefinition());
        Assertions.assertNotNull(handleResult.getHeaders());
        Assertions.assertNotNull(handleResult.getBody());

        MockRequestHandler.HandleError resultError = handleResult.getError();
        Assertions.assertNotNull(resultError);
        Assertions.assertEquals(errorType, resultError.getType());

        Exception exception = resultError.getException();
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MockHandleException.class, exception.getClass());
    }

    @Test
    @DisplayName("[OK] Safe invalid JSON body.")
    void testInvalidJsonBodyFormat() {
        TestMockUrlDefinition urlDefinition = new TestMockUrlDefinition("unknown-url-id-1", "/api/unknown");
        List<TestMockUrlDefinition> multipleUrlDefinitions = Collections.singletonList(urlDefinition);
        Mockito.when(urlProvider.getUrls())
                .thenAnswer(new GenericAnswer<>(multipleUrlDefinitions));

        TestMockResponseDefinition invalidJsonFormatResponse = TestMockResponseDefinition.builder()
                .status(HttpStatus.OK)
                .headers(emptyCompiledTemplate())
                .method(HttpMethod.POST)
                .condition("1 == 1")
                .order(0)
                .bodyType(ResponseBodyType.JSON)
                .body(template.compile(parserContext, "{aaa:"))
                .build();
        List<TestMockResponseDefinition> responses = Collections.singletonList(invalidJsonFormatResponse);
        Mockito.when(responseProvider.getResponses(urlDefinition, invalidJsonFormatResponse.getMethod()))
                .thenAnswer(new GenericAnswer<>(responses));

        MockErrorDefinition.Type errorType = MockErrorDefinition.Type.UNKNOWN;
        HttpStatus errorResponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        TestMockErrorDefinition errorDefinition = TestMockErrorDefinition.builder()
                .errorType(errorType)
                .status(errorResponseStatus)
                .headers(emptyCompiledTemplate())
                .bodyType(ResponseBodyType.TEXT)
                .body(emptyCompiledTemplate())
                .build();

        Mockito.when(errorProvider.getError(errorType))
                .thenReturn(errorDefinition);

        String uri = "/api/unknown";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        MockRequestHandler.HandleResult handleResult = handler.process(request, "");
        Assertions.assertFalse(handleResult.isSuccess());
        Assertions.assertEquals(uri, handleResult.getUri());
        Assertions.assertEquals(errorDefinition.getStatus(), handleResult.getStatus());
        Assertions.assertNotNull(handleResult.getDefinition());
        Assertions.assertNotNull(handleResult.getHeaders());
        Assertions.assertNotNull(handleResult.getBody());

        MockRequestHandler.HandleError resultError = handleResult.getError();
        Assertions.assertNotNull(resultError);
        Assertions.assertEquals(errorType, resultError.getType());

        Exception exception = resultError.getException();
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(MockHandleException.class, exception.getClass());
    }

    @Test
    @DisplayName("[OK] Invalid condition. Condition contains unknown field(or property)")
    void testInvalidConditionContainsUnknownField() {
        String uri = "/api/v1/test";
        TestMockUrlDefinition urlDefinition = new TestMockUrlDefinition("test-url-id", uri);
        List<TestMockUrlDefinition> multipleUrlDefinitions = Collections.singletonList(urlDefinition);
        Mockito.when(urlProvider.getUrls())
                .thenAnswer(new GenericAnswer<>(multipleUrlDefinitions));

        TestMockResponseDefinition responseDefinition = TestMockResponseDefinition.builder()
                .status(HttpStatus.OK)
                .headers(emptyCompiledTemplate())
                .condition("request.body.num eq 1")
                .method(HttpMethod.POST)
                .order(0)
                .bodyType(ResponseBodyType.JSON)
                .body(template.compile(parserContext, "{\"num\": @{request.body.num}}"))
                .build();
        List<TestMockResponseDefinition> responses = Collections.singletonList(responseDefinition);
        Mockito.when(responseProvider.getResponses(urlDefinition, responseDefinition.getMethod()))
                .thenAnswer(new GenericAnswer<>(responses));

        MockErrorDefinition.Type errorType = MockErrorDefinition.Type.NO_RESPONSE;
        TestMockErrorDefinition errorDefinition = TestMockErrorDefinition.builder()
                .errorType(MockErrorDefinition.Type.NO_RESPONSE)
                .status(HttpStatus.NO_CONTENT)
                .headers(emptyCompiledTemplate())
                .bodyType(ResponseBodyType.TEXT)
                .body(emptyCompiledTemplate())
                .build();

        Mockito.when(errorProvider.getError(errorType))
                .thenReturn(errorDefinition);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        request.setMethod(responseDefinition.getMethod().name());

        MockRequestHandler.HandleResult handleResult = handler.process(request, "");
        Assertions.assertFalse(handleResult.isSuccess());
        Assertions.assertEquals(uri, handleResult.getUri());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, handleResult.getStatus());
        Assertions.assertEquals(0, handleResult.getHeaders().size());
        Assertions.assertEquals(StringUtil.BLANK, handleResult.getBody());

        MockRequestHandler.HandleError error = handleResult.getError();
        Assertions.assertNotNull(error);
        Assertions.assertEquals(errorType, error.getType());
        Assertions.assertEquals(MockHandleNoResponseException.class, error.getException().getClass());

        MockRequestHandler.HandleDefinition definition = handleResult.getDefinition();
        Assertions.assertNotNull(definition);

        MockUrlDefinition url = definition.getUrl();
        Assertions.assertNotNull(url);
        Assertions.assertEquals(urlDefinition.getId(), url.getId());
        Assertions.assertEquals(urlDefinition.getUri(), url.getUri());

        Assertions.assertNull(definition.getResponse());

        List<ResponseMatcher.MatchResult> candidates = definition.getCandidates();
        Assertions.assertNotNull(candidates);
        Assertions.assertEquals(1, candidates.size());

        ResponseMatcher.MatchResult responseMatchResult = candidates.get(0);
        Assertions.assertFalse(responseMatchResult.isMatched());

        Exception exception = responseMatchResult.getException();
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(SpelEvaluationException.class, exception.getClass());

        MockResponseDefinition definition1 = responseMatchResult.getDefinition();
        Assertions.assertNotNull(definition1);
        Assertions.assertEquals(responseDefinition.getId(), definition1.getId());
        Assertions.assertEquals(responseDefinition.getCondition(), definition1.getCondition());
        Assertions.assertEquals(responseDefinition.getOrder(), definition1.getOrder());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestMockUrlDefinition implements MockUrlDefinition{
        private String id;
        private String uri;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestMockUrlDefinition that = (TestMockUrlDefinition) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    CompiledTemplate emptyCompiledTemplate() {
        return new CompiledTemplate("", Collections.emptyList());
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    static class TestMockResponseDefinition implements MockResponseDefinition {
        private final String id = UUID.randomUUID().toString();
        private HttpStatus status;
        private CompiledTemplate headers;
        private CompiledTemplate body;
        private String condition;
        private int order;
        private ResponseBodyType bodyType;
        private String description;
        private HttpMethod method;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    static class TestMockErrorDefinition implements MockErrorDefinition {
        private HttpStatus status;
        private Type errorType;
        private CompiledTemplate headers;
        private CompiledTemplate body;
        private ResponseBodyType bodyType;
    }
}
