package tienda.uni.api.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tienda.uni.api.persistence.entity.RoleEntity;
import tienda.uni.api.persistence.model.Role;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    /**
     * All roles are predefined, so you shouldn't have any trouble finding them.
     * That is why the entity class is being left as is, without <b></b>.
     */
    RoleEntity findByName(Role name);
}
