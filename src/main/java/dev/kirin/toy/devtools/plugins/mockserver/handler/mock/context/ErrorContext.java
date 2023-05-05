package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.context;

import dev.kirin.common.core.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ErrorContext {
    private RequestContext request;
    private Vars vars;
    private Error error;

    public static ErrorContext of(MockContext context, Exception e) {
        return ErrorContext.builder()
                .request(context.getRequest())
                .vars(context.getVars())
                .error(Error.of(e))
                .build();
    }

    public static ErrorContext of(RequestContext context, Class<? extends Exception> eClass, String message) {
        return ErrorContext.builder()
                .request(context)
                .error(Error.of(eClass, message))
                .build();
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Error {
        private String name;
        private StackTraceElement[] stackTrace;
        private String message;
        private String localizedMessage;

        public static Error of(Exception e) {
            return Error.builder()
                    .name(e.getClass().getName())
                    .stackTrace(e.getStackTrace())
                    .message(e.getMessage())
                    .localizedMessage(StringUtil.isEmpty(e.getLocalizedMessage(), e.getMessage()))
                    .build();
        }

        public static Error of(Class<? extends Exception> eClass, String message) {
            return Error.builder()
                    .name(eClass.getName())
                    .stackTrace(new StackTraceElement[]{})
                    .message(message)
                    .localizedMessage(message)
                    .build();
        }
    }
}
