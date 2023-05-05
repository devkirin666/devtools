package support.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@NoArgsConstructor
public class TestPageResponseVo<T> {
    private TestPageableImpl pageable;
    private long total;
    private List<T> contents;
    private Boolean hasContents;

    @Data
    @NoArgsConstructor
    public static class TestPageableImpl implements Pageable {
        private int pageSize;
        private int pageNumber;
        private long offset;

        @JsonProperty(value = "sort")
        @Getter(AccessLevel.PRIVATE)
        private TestSortIml sortImpl;
        private long totalPage;
        private boolean paged;
        private boolean unpaged;

        public TestPageableImpl(Pageable origin, long totalContents) {
            this.pageSize = origin.getPageSize();
            this.pageNumber = origin.getPageNumber();
            this.offset = origin.getOffset();
            this.sortImpl = new TestSortIml(origin.getSort());
            this.totalPage = (long) Math.ceil((double) totalContents / this.pageSize);
        }

        private TestPageableImpl(Pageable origin, int pageNumber, long totalPage) {
            this.pageSize = origin.getPageSize();
            this.offset = origin.getOffset();
            this.sortImpl = new TestSortIml(origin.getSort());
            this.pageNumber = pageNumber;
            this.totalPage = totalPage;
        }

        @Override
        public Pageable next() {
            return new TestPageableImpl(this, getPageNumber() + 1, getTotalPage());
        }

        @Override
        public Pageable previousOrFirst() {
            return new TestPageableImpl(this, Math.max(getPageNumber() - 1, 0), getTotalPage());
        }

        @Override
        public Pageable first() {
            return new TestPageableImpl(this, 0, getTotalPage());
        }

        @Override
        public Pageable withPage(int pageNumber) {
            return new TestPageableImpl(this, pageNumber, getTotalPage());
        }

        @Override
        public boolean hasPrevious() {
            return getPageNumber() > 0;
        }

        @Override
        public Sort getSort() {
            return Sort.unsorted();
        }
    }

    @Data
    @NoArgsConstructor
    public static class TestSortIml {
        private boolean sorted;
        private boolean unsorted;
        private boolean empty;

        public TestSortIml(Sort sort) {
            this.sorted = sort.isSorted();
            this.unsorted = sort.isUnsorted();
            this.empty = sort.isEmpty();
        }
    }
}
