package tienda.uni.api.post.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tienda.uni.api.post.persistence.entity.PublicationEntity;

import java.util.Optional;
import java.util.UUID;

public interface PublicationRepository extends JpaRepository<PublicationEntity, UUID>, JpaSpecificationExecutor<PublicationEntity> {

    @Query("SELECT post FROM PublicationEntity post JOIN FETCH post.salePerson sp JOIN FETCH sp.user u JOIN FETCH u.university uni WHERE post.id = :id AND uni.id = :universityId")
    Optional<PublicationEntity> findByIdAndUniversityId(@Param("id") UUID id, @Param("universityId") UUID universityId);
}
