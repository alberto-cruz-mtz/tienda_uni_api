package tienda.uni.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.persistence.entity.ProfileEntity;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
}
