package tienda.uni.api.post.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.post.persistence.entity.PublicationMediaEntity;

public interface PublicationMediaRepository extends JpaRepository<PublicationMediaEntity, Long> {
}
