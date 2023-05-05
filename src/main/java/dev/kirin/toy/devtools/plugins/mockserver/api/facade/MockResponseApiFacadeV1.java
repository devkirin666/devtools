package dev.kirin.toy.devtools.plugins.mockserver.api.facade;

import dev.kirin.common.spring.component.AbstractCrudFacade;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockResponseSearchParamVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockResponseVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseSearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.service.MockResponseCrudService;
import org.springframework.stereotype.Component;

@Component
public class MockResponseApiFacadeV1 extends AbstractCrudFacade<String
        , MockResponseDTO, MockResponseSearchParamDTO
        , MockResponseVoV1, MockResponseSearchParamVoV1
        , MockResponseCrudService> {

    @Override
    protected MockResponseVoV1 toVo(MockResponseDTO dto) {
        return MockResponseVoV1.of(dto);
    }
}
