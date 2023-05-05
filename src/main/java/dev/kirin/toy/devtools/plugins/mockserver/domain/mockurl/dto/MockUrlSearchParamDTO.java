package dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.dto;

import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.entity.MockUrlEntity;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.entity.MockUrlEntity_;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MockUrlSearchParamDTO implements SearchParamDTOModel<MockUrlEntity> {
    private String uri;
    private String name;
    private Boolean enabled;
    private String searchText;
    private Pageable pageable;

    @Override
    public List<Predicate> getPredicates(Root<MockUrlEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if(StringUtils.hasText(getUri())) {
            predicates.add(
                builder.like(root.get(MockUrlEntity_.uri), asLikeCondition(getUri()))
            );
        }
        if(StringUtils.hasText(getName())) {
            predicates.add(
                    builder.like(root.get(MockUrlEntity_.name), asLikeCondition(getName()))
            );
        }
        if(getEnabled() != null) {
            predicates.add(
                    builder.equal(root.get(MockUrlEntity_.enabled), getEnabled())
            );
        }
        if(StringUtils.hasText(getSearchText())) {
            predicates.add(
                builder.or(
                        builder.like(root.get(MockUrlEntity_.uri), asLikeCondition(getSearchText()))
                        , builder.like(root.get(MockUrlEntity_.name), asLikeCondition(getSearchText()))
                        , builder.equal(root.get(MockUrlEntity_.id), getSearchText())
                )
            );
        }
        return predicates;
    }
}
