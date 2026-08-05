package tienda.uni.api.auth.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import tienda.uni.api.auth.persistence.entity.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(UUID token);

    boolean existsByToken(UUID token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.token = :refreshToken")
    void markAsRevokedByToken(UUID refreshToken);
}
