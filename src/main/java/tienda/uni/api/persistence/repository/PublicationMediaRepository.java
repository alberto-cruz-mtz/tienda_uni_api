package tienda.uni.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.persistence.entity.PublicationMediaEntity;

public interface PublicationMediaRepository extends JpaRepository<PublicationMediaEntity, Long> {
}
