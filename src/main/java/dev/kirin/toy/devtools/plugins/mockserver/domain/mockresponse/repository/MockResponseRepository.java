package dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.repository;

import dev.kirin.common.spring.component.SimpleRepository;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.entity.MockResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MockResponseRepository extends SimpleRepository<String, MockResponseEntity> {
}
