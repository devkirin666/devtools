package dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kirin.common.template.Token;
import dev.kirin.common.template.compile.model.CompiledTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class MockResponseCompileItem extends CompiledTemplate implements Serializable {

    public MockResponseCompileItem(ObjectMapper objectMapper, Object data) throws JsonProcessingException {
        super(objectMapper.writeValueAsString(data), Collections.emptyList());
    }
    public MockResponseCompileItem(String content) {
        super(content, Collections.emptyList());
    }

    public MockResponseCompileItem(String content, Collection<Token> tokens) {
        super(content, tokens);
    }

    public <T> T asObject(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(getContent(), new TypeReference<T>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
