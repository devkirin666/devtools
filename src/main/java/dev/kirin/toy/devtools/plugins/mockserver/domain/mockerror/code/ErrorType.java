package dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code;

import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorType {
    NOT_FOUND("Not Found Error", HttpStatus.NOT_FOUND, MockErrorDefinition.Type.NOT_FOUND),
    NO_RESPONSE("No Responses", HttpStatus.NO_CONTENT, MockErrorDefinition.Type.NO_RESPONSE),
    MULTIPLE_URL("Multi URL Detect", HttpStatus.MULTIPLE_CHOICES, MockErrorDefinition.Type.MULTI_URL),
    UNKNOWN("Internal Error", HttpStatus.INTERNAL_SERVER_ERROR, MockErrorDefinition.Type.UNKNOWN),
    ;

    private final String title;
    private final HttpStatus defaultStatus;
    private final MockErrorDefinition.Type definitionType;

    private static final Map<MockErrorDefinition.Type, ErrorType> definitionTypeMap = Arrays.stream(values())
            .collect(Collectors.toMap(ErrorType::getDefinitionType, v -> v));

    public static ErrorType findBy(MockErrorDefinition.Type type) {
        return definitionTypeMap.getOrDefault(type, UNKNOWN);
    }
}
