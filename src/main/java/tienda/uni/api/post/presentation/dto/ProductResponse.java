package tienda.uni.api.post.presentation.dto;

import tienda.uni.api.post.persistence.model.SaleType;

public record ProductResponse(
        Integer quantity,
        Double price,
        SaleType saleType,
        boolean allowsLayaway,
        boolean isOutOfStock
) {
}
