package dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.service;

import dev.kirin.common.spring.component.AbstractCrudService;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.dto.MockUrlDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.dto.MockUrlSearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.entity.MockUrlEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.repository.MockUrlRepository;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockRequestHandler;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockUrlDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockUrlCrudService extends AbstractCrudService<String, MockUrlEntity
        , MockUrlDTO, MockUrlSearchParamDTO
        , MockUrlRepository> implements MockRequestHandler.UrlProvider {
    private static final String DOMAIN_NAME = "mock.url";

    @Override
    public String getDomainName() {
        return DOMAIN_NAME;
    }

    @Override
    protected MockUrlDTO toDTO(MockUrlEntity entity) {
        return MockUrlDTO.fromEntity(entity);
    }

    @Override
    protected String newId(MockUrlDTO param) {
        return uuid();
    }

    @Override
    public List<? extends MockUrlDefinition> getUrls() {
        return list(new MockUrlSearchParamDTO());
    }
}
