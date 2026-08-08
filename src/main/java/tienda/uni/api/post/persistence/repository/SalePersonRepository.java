package tienda.uni.api.post.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.post.persistence.entity.SalePersonEntity;

import java.util.UUID;

public interface SalePersonRepository extends JpaRepository<SalePersonEntity, UUID> {
}
