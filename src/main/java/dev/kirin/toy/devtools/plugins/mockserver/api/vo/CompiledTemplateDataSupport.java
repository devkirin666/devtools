package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.config.JsonConfig;
import org.springframework.http.HttpHeaders;

interface CompiledTemplateDataSupport {
    ObjectMapper OBJECT_MAPPER = JsonConfig.OBJECT_MAPPER;

    String getBody();
    HttpHeaders getHeaders();

    default CompiledTemplate toBody() {
        if(getBody() == null) {
            return null;
        }
        return CompiledTemplate.builder()
                .content(getBody())
                .build();
    }

    default CompiledTemplate toHeaders() {
        if(getHeaders() == null) {
            return null;
        }
        return CompiledTemplate.builder()
                .content(from(getHeaders()))
                .build();
    }

    static <T> T as(String json, Class<T> targetClass) {
        try {
            if(StringUtil.isEmpty(json)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(json, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T as(String json, TypeReference<T> typeReference) {
        try {
            if(StringUtil.isEmpty(json)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    default String from(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
