package dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.repository;

import dev.kirin.common.spring.component.SimpleRepository;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity.MockRequestHistoryEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity.MockRequestHistoryId;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestHistoryRepository extends SimpleRepository<MockRequestHistoryId, MockRequestHistoryEntity> {
}
