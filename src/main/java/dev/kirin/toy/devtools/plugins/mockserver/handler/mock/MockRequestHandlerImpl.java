package dev.kirin.toy.devtools.plugins.mockserver.handler.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kirin.common.template.Template;
import dev.kirin.common.template.builder.TemplateBuildContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockErrorHandler;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockRequestHandler;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.MockContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.RequestContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockResponseDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockUrlDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception.MockHandleDuplicateUrlsException;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception.MockHandleException;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.exception.MockHandleNoResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockRequestHandlerImpl implements MockRequestHandler {
    private final MockRequestHandler.UrlProvider urlProvider;
    private final MockRequestHandler.ResponseProvider responseProvider;
    private final MockErrorHandler errorHandler;
    private final Template template;
    private final ObjectMapper objectMapper;

    private final ResponseMatcher responseMatcher = new ResponseMatcher();

    @Override
    public HandleResult process(RequestContext request, MockUrlDefinition url) {
        MockHandleException error = null;
        MockUrlDefinition urlDefinition = url;
        String requestUri = request.getUri();
        List<ResponseMatcher.MatchResult> responses = Collections.emptyList();
        MockContext mockContext = new MockContext(request);
        try {
            List<? extends MockUrlDefinition> urlDefinitions = url != null ? Collections.singletonList(url) : urlProvider.getUrls();

            final AntPathMatcher pathMatcher = new AntPathMatcher();
            List<MockUrlDefinition> matchedUrls = urlDefinitions.stream()
                    .filter(matchedUrl -> pathMatcher.match(matchedUrl.getUri(), requestUri))
                    .collect(Collectors.toList());
            if (matchedUrls.size() > 1) {
                log.debug("(process) multiple url detected. uri = {}", requestUri);
                throw new MockHandleDuplicateUrlsException("Duplicated urls", matchedUrls);
            }

            urlDefinition = matchedUrls.stream()
                    .findAny()
                    .orElseThrow(() -> new MockHandleException("No matched urls", MockErrorDefinition.Type.NOT_FOUND));

            Map<String, String> pathVariables = pathMatcher.extractUriTemplateVariables(urlDefinition.getUri(), requestUri);
            mockContext.getRequest().setPathVariables(pathVariables);
            log.debug("(process) context = {}", mockContext);
            responses = responseMatcher.match(mockContext
                    , responseProvider.getResponses(urlDefinition, HttpMethod.valueOf(request.getMethod())));
            log.debug("(process) candidate responses = {}", responses.size());
            ResponseMatcher.MatchResult firstMatch = responses.stream()
                    .filter(ResponseMatcher.MatchResult::isMatched)
                    .findFirst()
                    .orElse(null);
            if (firstMatch == null) {
                log.debug("(process) no responses. url-id = {}", urlDefinition.getId());
                throw new MockHandleNoResponseException("No matched response.", responses);
            }

            MockResponseDefinition responseDefinition = firstMatch.getDefinition();
            TemplateBuildContext templateBuildContext = template.makeBuildContext(mockContext);
            String headerResult = template.build(templateBuildContext, responseDefinition.getHeaders());
            String bodyResult = template.build(templateBuildContext, responseDefinition.getBody());

            HttpHeaders responseHeader = HttpHeaders.EMPTY;
            if(StringUtils.hasText(headerResult)) {
                responseHeader = objectMapper.readValue(headerResult, HttpHeaders.class);
            }
            HandleDefinition definition = HandleDefinition.builder()
                    .url(url)
                    .response(responseDefinition)
                    .candidates(responses)
                    .build();
            return HandleResult.builder()
                    .success(true)
                    .uri(requestUri)
                    .status(responseDefinition.getStatus())
                    .headers(responseHeader)
                    .body(bodyResult)
                    .definition(definition)
                    .build();
        } catch (MockHandleException e) {
            error = e;
        } catch (Exception e) {
            error = new MockHandleException(e, MockErrorDefinition.Type.UNKNOWN);
        }
        log.info("(process) error. type = {}, cause = {}", error.getType(), error.getLocalizedMessage());
        log.debug("(process) error", error);

        MockErrorHandler.ErrorHandleResult errorResult = errorHandler.handle(mockContext, error);
        HandleDefinition definition = urlDefinition == null ? null : HandleDefinition.builder()
                .url(urlDefinition)
                .candidates(responses)
                .build();
        return HandleResult.builder()
                .success(false)
                .uri(requestUri)
                .status(errorResult.getStatus())
                .headers(errorResult.getHeaders())
                .body(errorResult.getBody())
                .definition(definition)
                .error(HandleError.of(errorResult))
                .build();
    }
}
