package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kirin.common.spring.constraint.CreateValidGroup;
import dev.kirin.common.spring.model.vo.VoModel;
import dev.kirin.toy.devtools.plugins.mockserver.code.ResponseBodyType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseDTO;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MockResponseVoV1 implements VoModel<MockResponseDTO>, CompiledTemplateDataSupport {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotEmpty(groups = CreateValidGroup.class)
    private String mockUrlId;

    @Min(value = 0, groups = CreateValidGroup.class)
    private Integer no;

    @NotNull(groups = CreateValidGroup.class)
    private HttpMethod method;

    @NotEmpty(groups = CreateValidGroup.class)
    private String condition;

    @Positive(groups = CreateValidGroup.class)
    @Min(HttpServletResponse.SC_OK)
    private int status;

    private String body;
    private ResponseBodyType bodyType;

    private HttpHeaders headers;

    @NotNull(groups = CreateValidGroup.class)
    private Boolean enabled;
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    @Override
    public MockResponseDTO toDTO() {
        return MockResponseDTO.builder()
                .no(getNo())
                .mockUrlId(getMockUrlId())
                .method(getMethod())
                .condition(getCondition())
                .status(HttpStatus.resolve(getStatus()))
                .body(toBody())
                .bodyType(getBodyType())
                .headers(toHeaders())
                .enabled(getEnabled())
                .description(getDescription())
                .build();
    }

    public static MockResponseVoV1 of(MockResponseDTO dto) {
        HttpHeaders headers = null;
        if(dto.getHeaders() != null) {
            headers = CompiledTemplateDataSupport.as(dto.getHeaders().getContent(), HttpHeaders.class);
        }
        if(headers == null) {
            headers = HttpHeaders.EMPTY;
        }
        return MockResponseVoV1.builder()
                .id(dto.getId())
                .no(dto.getNo())
                .mockUrlId(dto.getMockUrlId())
                .method(dto.getMethod())
                .condition(dto.getCondition())
                .status(dto.getStatus().value())
                .body(dto.getBody().getContent())
                .bodyType(dto.getBodyType())
                .headers(headers)
                .enabled(dto.getEnabled())
                .description(dto.getDescription())
                .lastUpdated(dto.getLastUpdated())
                .build();
    }
}
