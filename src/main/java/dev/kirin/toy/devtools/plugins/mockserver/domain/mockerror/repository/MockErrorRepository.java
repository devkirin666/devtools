package dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.repository;

import dev.kirin.common.spring.component.SimpleRepository;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.entity.MockErrorEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MockErrorRepository extends SimpleRepository<ErrorType, MockErrorEntity> {
}
