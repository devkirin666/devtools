package dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.entity;

import dev.kirin.common.spring.model.EntityModel;
import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.plugins.mockserver.domain.mockerror.code.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "mock_error")
public class MockErrorEntity implements EntityModel<ErrorType> {
    @Id
    @Comment("ID")
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ErrorType id;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    @Comment("response status name")
    private HttpStatus status;

    @Lob
    @Comment("compiled response headers")
    private CompiledTemplate headers;

    @Lob
    @Comment("compiled response body")
    private CompiledTemplate body;

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
