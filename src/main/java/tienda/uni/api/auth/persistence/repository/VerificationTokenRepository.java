package tienda.uni.api.auth.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.auth.persistence.entity.VerificationTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, UUID> {

    Optional<VerificationTokenEntity> findByToken(UUID token);
}
