package tienda.uni.api.presentation.dto;

import tienda.uni.api.persistence.model.SaleType;

public record ProductResponse(
        Integer quantity,
        Double price,
        SaleType saleType,
        boolean allowsLayaway,
        boolean isOutOfStock
) {
}
