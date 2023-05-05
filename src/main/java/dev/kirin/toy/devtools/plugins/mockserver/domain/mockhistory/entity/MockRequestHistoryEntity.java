package dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity;

import dev.kirin.common.spring.model.EntityModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto.RequestHistoryContentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "mock_response_history")
public class MockRequestHistoryEntity implements EntityModel<MockRequestHistoryId> {
    @EmbeddedId
    private MockRequestHistoryId id;

    private String responseId;

    @Lob
    @Convert(converter = RequestHistoryContentDTO.AttributeConverter.class)
    private RequestHistoryContentDTO content;
}
