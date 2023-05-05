package dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.service;

import dev.kirin.common.spring.component.AbstractCrudService;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseSearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.entity.MockResponseEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.repository.MockResponseRepository;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockRequestHandler;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockResponseDefinition;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockUrlDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockResponseCrudService extends AbstractCrudService<String, MockResponseEntity
        , MockResponseDTO, MockResponseSearchParamDTO
        , MockResponseRepository> implements MockRequestHandler.ResponseProvider {

    private static final String DOMAIN_NAME = "mock.response";

    @Override
    public String getDomainName() {
        return DOMAIN_NAME;
    }

    @Override
    protected MockResponseDTO toDTO(MockResponseEntity entity) {
        return MockResponseDTO.fromEntity(entity);
    }

    @Override
    protected String newId(MockResponseDTO param) {
        return uuid();
    }

    @Override
    public List<? extends MockResponseDefinition> getResponses(MockUrlDefinition urlDefinition, HttpMethod method) {
        MockResponseSearchParamDTO searchParamDTO = MockResponseSearchParamDTO.builder()
                .mockUrlIds(Collections.singleton(urlDefinition.getId()))
                .method(method)
                .build();
        return list(searchParamDTO);
    }
}
