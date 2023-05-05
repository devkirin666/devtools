package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.dto.MockUrlSearchParamDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
public class MockUrlSearchParamVoV1 implements SearchParamVoModel<MockUrlSearchParamDTO> {
    private String keyword;
    @Override
    public MockUrlSearchParamDTO toSearchParam(Pageable pageable) {
        return MockUrlSearchParamDTO.builder()
                .searchText(getKeyword())
                .pageable(pageable)
                .build();
    }
}
