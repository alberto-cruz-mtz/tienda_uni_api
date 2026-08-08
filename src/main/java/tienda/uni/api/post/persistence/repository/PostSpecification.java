package tienda.uni.api.post.persistence.repository;

import jakarta.persistence.criteria.JoinType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import tienda.uni.api.post.persistence.entity.PublicationEntity;

import java.util.UUID;

public class PostSpecification {

    public static Specification<PublicationEntity> fetchRelations() {
        return ((root, query, criteriaBuilder) -> {
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                // Traemos las relaciones OneToOne y ManyToOne (Relaciones simples)
                root.fetch("product", JoinType.LEFT);

                // Podemos encadenar fetch para traer el vendedor y su usuario
                root.fetch("salePerson", JoinType.INNER)
                        .fetch("user", JoinType.INNER);
            }

            return criteriaBuilder.conjunction();
        });
    }

    public static Specification<PublicationEntity> getOnlyPublicationsByUniversity(UUID universityId) {
        return ((root, query, criteriaBuilder) -> {
            if (universityId == null) throw new IllegalArgumentException("University ID cannot be null");

            return criteriaBuilder.equal(root.get("salePerson").get("user").get("university").get("id"), universityId);
        });
    }

    public static Specification<PublicationEntity> searchByTitle(String searchTerm) {
        return ((root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + searchTerm.toLowerCase() + "%"
            );
        });
    }

    public static Specification<PublicationEntity> filterByStock(Boolean isOutOfStock) {
        return ((root, query, criteriaBuilder) -> {
            if (isOutOfStock == null) {
                return null;
            }

            if (isOutOfStock) {
                return criteriaBuilder.equal(root.get("product").get("inventory"), 0);
            } else {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("product").get("inventory"), 0);
            }
        });
    }
}
