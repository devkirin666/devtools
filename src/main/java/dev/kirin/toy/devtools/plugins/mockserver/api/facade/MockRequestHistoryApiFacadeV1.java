package dev.kirin.toy.devtools.plugins.mockserver.api.facade;

import dev.kirin.common.spring.component.AbstractSearchFacade;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.RequestHistorySearchParamVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.RequestHistoryVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto.RequestHistoryDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto.RequestHistorySearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.service.RequestHistoryService;
import org.springframework.stereotype.Component;

@Component
public class MockRequestHistoryApiFacadeV1 extends AbstractSearchFacade<RequestHistoryDTO, RequestHistorySearchParamDTO
        , RequestHistoryVoV1, RequestHistorySearchParamVoV1
        , RequestHistoryService> {
    @Override
    protected RequestHistoryVoV1 toVo(RequestHistoryDTO dto) {
        return RequestHistoryVoV1.of(dto);
    }

    public void deleteAll(RequestHistorySearchParamVoV1 searchParam) {
        service.deleteAll(searchParam.toSearchParam());
    }
}
