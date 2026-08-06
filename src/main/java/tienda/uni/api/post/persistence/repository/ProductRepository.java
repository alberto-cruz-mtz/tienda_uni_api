package tienda.uni.api.post.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.post.persistence.entity.ProductEntity;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
