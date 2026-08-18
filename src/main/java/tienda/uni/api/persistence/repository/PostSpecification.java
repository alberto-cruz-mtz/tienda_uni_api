package tienda.uni.api.persistence.repository;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tienda.uni.api.persistence.entity.PublicationEntity;

import java.time.Instant;
import java.util.UUID;

public class PostSpecification {

    public static Specification<PublicationEntity> getOnlyPublicationsByIdAndPostedAtLessThat(UUID id, Instant postedAt) {
        return ((root, query, criteriaBuilder) -> {
            if (id == null || postedAt == null) return criteriaBuilder.conjunction();

            Predicate postedAtLess = criteriaBuilder.lessThan(root.get("postedAt"), postedAt);
            Predicate postedAtEqual = criteriaBuilder.equal(root.get("postedAt"), postedAt);
            Predicate idLess = criteriaBuilder.lessThan(root.get("id"), id);
            Predicate tieBreakerPredicate = criteriaBuilder.and(postedAtEqual, idLess);

            return criteriaBuilder.or(postedAtLess, tieBreakerPredicate);
        });
    }

    public static Specification<PublicationEntity> fetchRelations() {
        return ((root, query, criteriaBuilder) -> {
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("product", JoinType.LEFT);
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
