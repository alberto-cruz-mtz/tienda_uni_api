package tienda.uni.api.post.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.post.persistence.entity.TagEntity;
import tienda.uni.api.post.persistence.model.TagName;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    Set<TagEntity> findTagEntitiesByNameIn(Collection<TagName> names);
}