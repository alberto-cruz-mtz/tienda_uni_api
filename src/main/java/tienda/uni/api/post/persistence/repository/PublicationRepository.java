package tienda.uni.api.post.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tienda.uni.api.post.persistence.entity.PublicationEntity;

import java.util.UUID;

public interface PublicationRepository extends JpaRepository<PublicationEntity, UUID>, JpaSpecificationExecutor<PublicationEntity> {

    Slice<PublicationEntity> findAllBySalePerson_User_University_Id(UUID salePersonUserUniversityId, Pageable pageable);
}
