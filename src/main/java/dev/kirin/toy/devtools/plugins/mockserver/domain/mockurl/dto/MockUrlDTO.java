package dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.dto;

import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.entity.MockUrlEntity;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockUrlDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockUrlDTO implements DTOModel<String, MockUrlEntity>, MockUrlDefinition {
    private String id;
    private String uri;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalDateTime lastUpdated;

    public MockUrlEntity toEntity() {
        return MockUrlEntity.builder()
                .uri(getUri())
                .name(getName())
                .description(getDescription())
                .enabled(getEnabled())
                .lastUpdated(getLastUpdated())
                .build();
    }

    public static MockUrlDTO fromEntity(MockUrlEntity entity) {
        if(entity == null) {
            return null;
        }
        return MockUrlDTO.builder()
                .id(entity.getId())
                .uri(entity.getUri())
                .name(entity.getName())
                .description(entity.getDescription())
                .enabled(entity.getEnabled())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }
}
