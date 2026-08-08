package tienda.uni.api.post.util;

import org.springframework.stereotype.Component;
import tienda.uni.api.post.persistence.entity.ProductEntity;
import tienda.uni.api.post.persistence.entity.PublicationEntity;
import tienda.uni.api.post.presentation.dto.ProductRequest;
import tienda.uni.api.post.presentation.dto.ProductResponse;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Component
public class ProductMapper {

    public ProductEntity fromDto(ProductRequest dto) {
        return ProductEntity.builder()
                .salePrice(new BigDecimal(dto.price()))
                .inventory(new BigDecimal(dto.quantity()))
                .saleType(dto.typeSale())
                .allowsLayaway(dto.allowsLayaway())
                .build();
    }

    public ProductEntity toEntity(ProductRequest dto, PublicationEntity publicationEntity) {
        var product = this.fromDto(dto);
        product.setPublication(publicationEntity);
        return product;
    }

    public ProductResponse fromEntity(ProductEntity entity) {
        return new ProductResponse(
                entity.getInventory().intValue(),
                entity.getSalePrice().doubleValue(),
                entity.getSaleType(),
                entity.isAllowsLayaway(),
                entity.getInventory().compareTo(BigDecimal.ZERO) == 0
        );
    }
}
