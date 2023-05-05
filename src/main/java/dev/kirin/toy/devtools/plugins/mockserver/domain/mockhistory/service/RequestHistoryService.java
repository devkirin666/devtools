package dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.service;

import dev.kirin.common.spring.component.AbstractCrudService;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto.RequestHistoryDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto.RequestHistorySearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity.MockRequestHistoryEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity.MockRequestHistoryId;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.repository.RequestHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RequestHistoryService extends AbstractCrudService<MockRequestHistoryId, MockRequestHistoryEntity, RequestHistoryDTO, RequestHistorySearchParamDTO, RequestHistoryRepository> {
    private static final String DOMAIN_NAME = "mock.history";

    @Override
    public String getDomainName() {
        return DOMAIN_NAME;
    }
    @Override
    protected RequestHistoryDTO toDTO(MockRequestHistoryEntity entity) {
        return RequestHistoryDTO.fromEntity(entity);
    }

    @Override
    protected MockRequestHistoryId newId(RequestHistoryDTO param) {
        return new MockRequestHistoryId(LocalDateTime.now(), param.getMockUrlId());
    }
}
