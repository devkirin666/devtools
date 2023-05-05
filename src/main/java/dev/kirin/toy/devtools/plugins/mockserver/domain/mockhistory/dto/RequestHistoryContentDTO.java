package dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kirin.common.spring.persistence.converter.JsonAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestHistoryContentDTO {
    private String url;
    private Content request;
    private Content response;

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Content {
        private HttpHeaders headers;
        private MultiValueMap<String, Object> params;
        private String body;
    }

    public static class AttributeConverter extends JsonAttributeConverter<RequestHistoryContentDTO> {

        public AttributeConverter(ObjectMapper objectMapper) {
            super(objectMapper);
        }
    }
}
