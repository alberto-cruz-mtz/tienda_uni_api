package tienda.uni.api.post.presentation.dto;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record PostParams(
        Pageable pageable,
        UUID universityId,
        String search,
        boolean isOutOfStock
) {
}
