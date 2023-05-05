package dev.kirin.toy.devtools.plugins.mockserver.api.facade;

import dev.kirin.common.spring.component.AbstractFixedFacade;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockErrorSearchParamVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockErrorVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.dto.MockErrorDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.dto.MockErrorSearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.service.MockErrorFixedService;
import org.springframework.stereotype.Component;

@Component
public class MockErrorApiFacadeV11 extends AbstractFixedFacade<ErrorType
        , MockErrorDTO, MockErrorSearchParamDTO
        , MockErrorVoV1, MockErrorSearchParamVoV1
        , MockErrorFixedService> {
    @Override
    protected MockErrorVoV1 toVo(MockErrorDTO dto) {
        return MockErrorVoV1.of(dto);
    }
}
