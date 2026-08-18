package tienda.uni.api.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaginationMetadata(
        int limit,
        boolean hasMore,
        String next // puede ser NULL si ya ha llegado a la ultima pagina
) {
}
