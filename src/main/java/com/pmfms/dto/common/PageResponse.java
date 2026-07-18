package com.pmfms.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/** Standard pagination envelope for all list APIs (BRD 11.3). */
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {

    private List<T> content;

    @Schema(example = "0")
    private int page;

    @Schema(example = "20")
    private int size;

    @Schema(example = "135")
    private long totalElements;

    @Schema(example = "7")
    private int totalPages;

    private boolean last;

    public static <T> PageResponse<T> of(Page<?> page, List<T> content) {
        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
