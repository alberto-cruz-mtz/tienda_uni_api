package tienda.uni.api.post.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.post.persistence.entity.TagEntity;

public interface TagRepository extends JpaRepository<TagEntity, Integer> {
}
