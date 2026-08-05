package tienda.uni.api.post.presentation.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        String title,
        String description,
        List<MediaContentRequest> mediaContent,
        ProductRequest product,
        Instant createdAt,
        Instant updatedAt
) {
}