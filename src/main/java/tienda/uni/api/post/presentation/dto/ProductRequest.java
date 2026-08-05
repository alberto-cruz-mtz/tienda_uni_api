package tienda.uni.api.post.presentation.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import tienda.uni.api.post.persistence.model.SaleType;

public record ProductRequest(
        @NotNull(message = "La cantidad es obligatoria.")
        Integer quantity,

        @NotNull(message = "El precio es obligatorio.")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor o igual a 0.01.")
        @DecimalMax(value = "9999999.99", message = "El precio debe ser menor o igual a 9,999,999.99.")
        @Digits(integer = 7, fraction = 2, message = "El precio debe tener como máximo 2 decimales.")
        Double price,

        @NotNull(message = "El tipo de venta es obligatorio.")
        SaleType typeSale,

        @NotNull(message = "El campo allowsLayaway es obligatorio.")
        Boolean allowsLayaway
) {
        @AssertTrue(message = "Si el tipo de venta es BY_QUANTITY la cantidad debe estar entre 1 y 1,000,000; en caso contrario debe ser -1.")
        public boolean isQuantityConsistentWithTypeSale() {
                if (quantity == null || typeSale == null) {
                        return true;
                }
                if (typeSale == SaleType.BY_QUANTITY) {
                        return quantity >= 1 && quantity <= 1_000_000;
                }
                return quantity == -1;
        }
}