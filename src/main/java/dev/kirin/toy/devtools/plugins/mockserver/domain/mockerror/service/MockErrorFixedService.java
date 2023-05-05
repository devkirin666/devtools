package dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kirin.common.spring.component.AbstractFixedService;
import dev.kirin.common.spring.model.vo.ApiErrorVo;
import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.dto.MockErrorDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.dto.MockErrorSearchParamDTO;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.entity.MockErrorEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.repository.MockErrorRepository;
import dev.kirin.toy.devtools.plugins.mockserver.handler.MockRequestHandler;
import dev.kirin.toy.devtools.plugins.mockserver.handler.mock.definition.MockErrorDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MockErrorFixedService extends AbstractFixedService<ErrorType, MockErrorEntity
        , MockErrorDTO, MockErrorSearchParamDTO
        , MockErrorRepository> implements MockRequestHandler.ErrorProvider {

    private static final String DOMAIN_NAME = "mock.error";

    private final ObjectMapper objectMapper;

    @PostConstruct
    void initializedData() {
        Map<ErrorType, MockErrorEntity> entityMap = repository.findAll()
                .stream()
                .collect(Collectors.toMap(MockErrorEntity::getId, entity -> entity));
        Set<MockErrorEntity> newEntities = Arrays.stream(ErrorType.values())
                .filter(type -> !entityMap.containsKey(type))
                .map(this::newEntity)
                .collect(Collectors.toSet());
        repository.saveAllAndFlush(newEntities);
    }

    @Override
    public String getDomainName() {
        return DOMAIN_NAME;
    }


    @Override
    protected MockErrorDTO toDTO(MockErrorEntity entity) {
        return MockErrorDTO.fromEntity(entity);
    }

    private MockErrorEntity newEntity(ErrorType type){
        ApiErrorVo errorBody = ApiErrorVo.builder()
                .type("@{error.name}")
                .title(type.getTitle())
                .status(type.getDefaultStatus().value())
                .detail("@{error.localizedMessage}")
                .instance("@{request.uri}")
                .build();
        try {
            return MockErrorEntity.builder()
                    .id(type)
                    .status(type.getDefaultStatus())
                    .headers(new CompiledTemplate(null, Collections.emptyList()))
                    .body(new CompiledTemplate(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorBody), Collections.emptyList()))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MockErrorDefinition getError(MockErrorDefinition.Type type) {
        return get(ErrorType.findBy(type));
    }
}
