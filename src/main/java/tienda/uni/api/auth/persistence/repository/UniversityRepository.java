package tienda.uni.api.auth.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tienda.uni.api.auth.persistence.entity.UniversityEntity;

import java.util.Optional;
import java.util.UUID;

public interface UniversityRepository extends JpaRepository<UniversityEntity, UUID> {

    @Query("SELECT u FROM UniversityEntity u JOIN u.emailDomains d WHERE d = :domain")
    Optional<UniversityEntity> findBySpecificDomain(@Param("domain") String domain);
}
