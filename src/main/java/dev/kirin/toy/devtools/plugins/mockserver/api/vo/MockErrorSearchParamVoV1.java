package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.dto.MockErrorSearchParamDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
public class MockErrorSearchParamVoV1 implements SearchParamVoModel<MockErrorSearchParamDTO> {
    @Override
    public MockErrorSearchParamDTO toSearchParam(Pageable pageable) {
        return MockErrorSearchParamDTO.builder()
                .pageable(pageable)
                .build();
    }
}
