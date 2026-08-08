package tienda.uni.api.post.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaginationMetadata(
        int page,
        int limit,
        int count,
        String next // puede ser NULL si ya ha llegado a la ultima pagina
) {
}
