package dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.dto;

import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.entity.MockResponseEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.entity.MockResponseEntity_;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockResponseSearchParamDTO implements SearchParamDTOModel<MockResponseEntity> {
    private Set<String> mockUrlIds;
    private HttpMethod method;
    private Boolean enabled;

    private Pageable pageable;
    @Override
    public List<Predicate> getPredicates(Root<MockResponseEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if(!CollectionUtils.isEmpty(getMockUrlIds())) {
            predicates.add(
                root.get(MockResponseEntity_.mockUrlId).in(getMockUrlIds())
            );
        }
        if(getMethod() != null) {
            predicates.add(
                builder.equal(root.get(MockResponseEntity_.method), getMethod())
            );
        }
        if(getEnabled() != null) {
            predicates.add(
                    builder.equal(root.get(MockResponseEntity_.enabled), getEnabled())
            );
        }
        return predicates;
    }
}
