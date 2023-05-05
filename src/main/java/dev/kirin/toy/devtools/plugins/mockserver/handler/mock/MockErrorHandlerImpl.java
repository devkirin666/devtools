package dev.kirin.toy.devtools.plugins.mockserver.handler.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.template.Template;
import dev.kirin.common.template.builder.TemplateBuildContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockErrorHandler;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockRequestHandler;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.ErrorContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context.MockContext;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockErrorHandlerImpl implements MockErrorHandler {
    private final MockRequestHandler.ErrorProvider errorProvider;
    private final Template template;
    private final ObjectMapper objectMapper;

    @Override
    public ErrorHandleResult handle(MockContext context, MockErrorDefinition.Type type, Exception exception) {
        MockErrorDefinition definition = errorProvider.getError(type);
        ErrorContext errorContext = ErrorContext.of(context, exception);
        TemplateBuildContext errorBuildContext = template.makeBuildContext(errorContext);

        HttpHeaders headers = HttpHeaders.EMPTY;
        String headerJson = definition.getHeaders().getContent();
        String body = definition.getBody().getContent();
        try {
            headerJson = template.build(errorBuildContext, definition.getHeaders());
            body = template.build(errorBuildContext, definition.getBody());
        } catch (Exception e) {
            log.error("(handle) Fail bind error response data. Cause: {}", e.getLocalizedMessage());
            log.debug("(handle) stack-trace ", e);
        } finally {
            try {
                if(StringUtil.hasText(headerJson)) {
                    headers = objectMapper.readValue(headerJson, HttpHeaders.class);
                }
            } catch (JsonProcessingException e) {
                log.debug("(handle) Cannot convert header to json. cause = {}", e.getLocalizedMessage());
            }
        }
        return ErrorHandleResult.builder()
                .type(definition.getErrorType())
                .status(definition.getStatus())
                .headers(headers)
                .body(body)
                .exception(exception)
                .build();
    }
}
