package tienda.uni.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.persistence.entity.SalePersonEntity;

import java.util.UUID;

public interface SalePersonRepository extends JpaRepository<SalePersonEntity, UUID> {
}
