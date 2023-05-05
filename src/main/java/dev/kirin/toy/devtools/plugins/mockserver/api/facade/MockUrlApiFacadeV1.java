package dev.kirin.toy.devtools.plugins.mockserver.api.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kirin.common.spring.component.AbstractCrudFacade;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlSearchParamVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlTestVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.api.vo.MockUrlVoV1;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseSearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto.MockResponseTestContext;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.service.MockResponseCrudService;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.dto.MockUrlDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.dto.MockUrlSearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.service.MockUrlCrudService;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockRequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockUrlApiFacadeV1 extends AbstractCrudFacade<String
        , MockUrlDTO, MockUrlSearchParamDTO
        , MockUrlVoV1, MockUrlSearchParamVoV1
        , MockUrlCrudService> {

    private final MockResponseCrudService responseService;
    private final MockRequestHandler mockRequestHandler;
    private final ObjectMapper objectMapper;

    @Override
    protected MockUrlVoV1 toVo(MockUrlDTO dto) {
        return MockUrlVoV1.of(dto);
    }

    @Override
    @Transactional
    public void delete(String id) {
        MockResponseSearchParamDTO responseSearchParam = MockResponseSearchParamDTO.builder()
                .mockUrlIds(Collections.singleton(id))
                .build();
        Set<String> responseIds = responseService.list(responseSearchParam)
                .stream()
                .map(MockResponseDTO::getId)
                .collect(Collectors.toSet());
        responseIds.forEach(responseService::delete);
        service.delete(id);
    }

    public MockUrlTestVoV1.Response testUrl(String id, MockUrlTestVoV1.Request body) {
        MockResponseTestContext testContext = body.asContextData();
        Object contextBody = testContext.getBody();
        log.debug("(testUrl) context body = {}", contextBody);
        if(contextBody instanceof String) {
            try {
                contextBody = objectMapper.readValue((String) contextBody, Object.class);
                testContext.setBody(contextBody);
            } catch (JsonProcessingException e) {
                log.debug("(testUrl) Not json body or invalid. cause = {}", e.getLocalizedMessage());
            }
        }
        MockUrlDTO mockUrl = service.get(id);
        MockRequestHandler.HandleResult handleResult = mockRequestHandler.process(testContext, mockUrl);
        return MockUrlTestVoV1.Response.of(id, mockUrl.getName(), handleResult);
    }
}
