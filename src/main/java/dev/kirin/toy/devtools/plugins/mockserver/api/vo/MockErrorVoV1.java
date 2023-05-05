package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kirin.common.spring.model.vo.VoModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.dto.MockErrorDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockErrorVoV1 implements VoModel<MockErrorDTO>, CompiledTemplateDataSupport {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ErrorType type;

    @Positive
    @Min(200)
    private int status;

    private HttpHeaders headers;

    private String body;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    @Override
    public MockErrorDTO toDTO() {
        return MockErrorDTO.builder()
                .type(getType())
                .status(HttpStatus.valueOf(getStatus()))
                .headers(toHeaders())
                .body(toBody())
                .build();
    }

    public static MockErrorVoV1 of(MockErrorDTO dto) {
        HttpHeaders headers = null;
        if(dto.getHeaders() != null) {
            headers = CompiledTemplateDataSupport.as(dto.getHeaders().getContent(), HttpHeaders.class);
        }
        if(headers == null) {
            headers = HttpHeaders.EMPTY;
        }
        return MockErrorVoV1.builder()
                .type(dto.getType())
                .status(dto.getStatus().value())
                .headers(headers)
                .body(dto.getBody().getContent())
                .lastUpdated(dto.getLastUpdated())
                .build();
    }
}
