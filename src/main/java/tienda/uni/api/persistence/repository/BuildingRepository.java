package tienda.uni.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.persistence.entity.BuildingEntity;

import java.util.UUID;

public interface BuildingRepository extends JpaRepository<BuildingEntity, UUID> {
}
