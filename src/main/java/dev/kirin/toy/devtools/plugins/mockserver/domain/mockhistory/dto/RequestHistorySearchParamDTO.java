package dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.dto;

import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity.MockRequestHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestHistorySearchParamDTO implements SearchParamDTOModel<MockRequestHistoryEntity> {
    private LocalDateTime from;
    private LocalDateTime to;
    private Pageable pageable;

    @Override
    public List<Predicate> getPredicates(Root<MockRequestHistoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }
}
