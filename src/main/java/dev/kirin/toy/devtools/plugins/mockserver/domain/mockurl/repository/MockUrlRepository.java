package dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.repository;

import dev.kirin.common.spring.component.SimpleRepository;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.entity.MockUrlEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MockUrlRepository extends SimpleRepository<String, MockUrlEntity> {
}
