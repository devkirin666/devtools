package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kirin.common.spring.constraint.CreateValidGroup;
import dev.kirin.common.spring.model.vo.VoModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.dto.MockUrlDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockUrlVoV1 implements VoModel<MockUrlDTO> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotEmpty(groups = CreateValidGroup.class)
    private String uri;
    @NotEmpty(groups = CreateValidGroup.class)
    private String name;
    private String description;
    @NotNull(groups = CreateValidGroup.class)
    private Boolean enabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastUpdated;

    public static MockUrlVoV1 of(MockUrlDTO dto) {
        return MockUrlVoV1.builder()
                .id(dto.getId())
                .uri(dto.getUri())
                .name(dto.getName())
                .description(dto.getDescription())
                .enabled(dto.getEnabled())
                .lastUpdated(dto.getLastUpdated())
                .build();
    }

    @Override
    public MockUrlDTO toDTO() {
        return MockUrlDTO.builder()
                .uri(getUri())
                .name(getName())
                .description(getDescription())
                .enabled(getEnabled())
                .build();
    }
}
