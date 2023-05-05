package dev.kirin.toy.devtools.plugins.mockserver.domain.mockhistory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Embeddable
public class MockRequestHistoryId implements Serializable {
    @NotNull
    private LocalDateTime historyDate;

    @Column(length = 32)
    @NotEmpty
    private String mockUrlId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MockRequestHistoryId that = (MockRequestHistoryId) o;
        return Objects.equals(historyDate, that.historyDate) && Objects.equals(mockUrlId, that.mockUrlId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(historyDate, mockUrlId);
    }
}
