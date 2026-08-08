package tienda.uni.api.post.presentation.dto;

import java.util.List;

public record DataResponse<T>(
        PaginationMetadata metadata,
        List<T> data
) {
}
