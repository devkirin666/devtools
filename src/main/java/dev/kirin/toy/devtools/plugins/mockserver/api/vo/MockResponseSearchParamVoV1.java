package dev.kirin.toy.devtools.plugins.mockserver.api.vo;

import dev.kirin.common.spring.constraint.SearchValidGroup;
import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseSearchParamDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotEmpty;
import java.util.Collections;

@Data
@NoArgsConstructor
public class MockResponseSearchParamVoV1 implements SearchParamVoModel<MockResponseSearchParamDTO> {
    @NotEmpty(groups = {SearchValidGroup.class})
    private String mockUrlId;
    @Override
    public MockResponseSearchParamDTO toSearchParam(Pageable pageable) {
        return MockResponseSearchParamDTO.builder()
                .mockUrlIds(Collections.singleton(getMockUrlId()))
                .pageable(pageable)
                .build();
    }
}
