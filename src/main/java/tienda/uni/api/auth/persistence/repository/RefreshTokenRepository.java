package tienda.uni.api.auth.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.auth.persistence.entity.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(UUID token);
}
