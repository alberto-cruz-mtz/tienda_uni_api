package tienda.uni.api.post.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.post.persistence.entity.PublicationEntity;

import java.util.UUID;

public interface PublicationRepository extends JpaRepository<PublicationEntity, UUID> {
}
