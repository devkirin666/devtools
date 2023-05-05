package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import dev.kirin.common.spring.model.vo.VoModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto.RequestHistoryContentDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto.RequestHistoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestHistoryVoV1 implements VoModel<RequestHistoryDTO> {
    private LocalDateTime historyDate;
    private String mockUrlId;
    private String responseId;
    private RequestHistoryContentDTO content;

    public static RequestHistoryVoV1 of(RequestHistoryDTO dto) {
        return RequestHistoryVoV1.builder()
                .historyDate(dto.getHistoryDate())
                .mockUrlId(dto.getMockUrlId())
                .responseId(dto.getResponseId())
                .content(dto.getContent())
                .build();
    }

    @Override
    public RequestHistoryDTO toDTO() {
        return RequestHistoryDTO.builder()
                .historyDate(getHistoryDate())
                .mockUrlId(getMockUrlId())
                .responseId(getMockUrlId())
                .content(getContent())
                .build();
    }
}
