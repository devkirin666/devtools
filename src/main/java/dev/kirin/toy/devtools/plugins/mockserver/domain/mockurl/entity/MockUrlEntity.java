package dev.kirin.toy.devtools.plugins.mockserver.domain.mockurl.entity;

import dev.kirin.common.spring.model.EntityModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "mock_url"
    , uniqueConstraints = {@UniqueConstraint(name = "uk_mock_url_name", columnNames = {"name"})}
)
public class MockUrlEntity implements EntityModel<String> {
    @Id
    @Comment("ID")
    @Column(length = 32)
    private String id;

    @NotEmpty
    @Comment("URI")
    private String uri;

    @Column(length = 100)
    @Comment("URL name")
    private String name;

    @Lob
    @Comment("description")
    private String description;

    private Boolean enabled;

    @Comment("Last updated date/time")
    private LocalDateTime lastUpdated;

    @PrePersist
    void prePersist() {
        this.lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
