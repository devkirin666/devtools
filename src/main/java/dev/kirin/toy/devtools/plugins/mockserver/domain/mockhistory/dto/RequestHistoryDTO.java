package dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto;

import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity.MockRequestHistoryEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity.MockRequestHistoryId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestHistoryDTO implements DTOModel<MockRequestHistoryId, MockRequestHistoryEntity> {
    private LocalDateTime historyDate;
    private String mockUrlId;
    private String responseId;
    private RequestHistoryContentDTO content;

    public static RequestHistoryDTO fromEntity(MockRequestHistoryEntity entity) {
        return RequestHistoryDTO.builder()
                .historyDate(entity.getId().getHistoryDate())
                .mockUrlId(entity.getId().getMockUrlId())
                .responseId(entity.getResponseId())
                .content(entity.getContent())
                .build();
    }

    @Override
    public MockRequestHistoryId getId() {
        return new MockRequestHistoryId(getHistoryDate(), getMockUrlId());
    }

    @Override
    public MockRequestHistoryEntity toEntity() {
        return MockRequestHistoryEntity.builder()
                .responseId(getResponseId())
                .content(getContent())
                .build();
    }
}
