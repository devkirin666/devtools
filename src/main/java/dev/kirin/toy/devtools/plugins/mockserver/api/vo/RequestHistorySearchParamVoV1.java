package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto.RequestHistorySearchParamDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestHistorySearchParamVoV1 implements SearchParamVoModel<RequestHistorySearchParamDTO> {
    private LocalDateTime from;
    private LocalDateTime to;

    @Override
    public RequestHistorySearchParamDTO toSearchParam(Pageable pageable) {
        return RequestHistorySearchParamDTO.builder()
                .from(getFrom())
                .to(getTo())
                .pageable(pageable)
                .build();
    }
}
