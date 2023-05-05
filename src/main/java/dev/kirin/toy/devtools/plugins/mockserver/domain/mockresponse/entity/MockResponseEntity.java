package dev.kirin.toy.devtools.plugins.mockserver.domain.mockresponse.entity;

import dev.kirin.common.spring.model.EntityModel;
import dev.kirin.common.template.compile.model.CompiledTemplate;
import dev.kirin.toy.devtools.plugins.mockserver.code.ResponseBodyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.http.HttpMethod;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "mock_response"
    , indexes = {@Index(name = "idx_mock_response_url_id", columnList = "mockUrlId")}
)
public class MockResponseEntity implements EntityModel<String> {
    @Id
    @Column(length = 32)
    private String id;

    @Column(length = 32)
    private String mockUrlId;

    @Comment("Order priority.")
    private Integer no;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    @Comment("request method name")
    private HttpMethod method;

    @Lob
    @Comment("Condition for response")
    private String condition;

    @Lob
    @Comment("description")
    private String description;

    @Comment("response status code")
    private Integer status;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ResponseBodyType bodyType;

    @Lob
    @Comment("compiled response body")
    private CompiledTemplate body;

    @Lob
    @Comment("compiled response header")
    private CompiledTemplate headers;

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
