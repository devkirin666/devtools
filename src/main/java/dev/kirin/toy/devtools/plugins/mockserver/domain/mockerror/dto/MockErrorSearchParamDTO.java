package dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.dto;

import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.entity.MockErrorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockErrorSearchParamDTO implements SearchParamDTOModel<MockErrorEntity> {
    private Pageable pageable;
    @Override
    public List<Predicate> getPredicates(Root<MockErrorEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return Collections.emptyList();
    }
}
