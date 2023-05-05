package dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition;

import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.plugins.mockserver.code.ResponseBodyType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public interface MockResponseDefinition extends Comparable<MockResponseDefinition> {
    String getId();
    HttpStatus getStatus();
    CompiledTemplate getHeaders();
    ResponseBodyType getBodyType();
    CompiledTemplate getBody();
    String getCondition();
    int getOrder();
    String getDescription();
    HttpMethod getMethod();

    @Override
    default int compareTo(MockResponseDefinition o) {
        return Integer.compare(getOrder(), o.getOrder());
    }
}
