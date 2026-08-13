package tienda.uni.api.presentation.dto;

import tienda.uni.api.persistence.model.TagName;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        String title,
        String description,
        List<TagName> tags,
        List<MediaContentRequest> mediaContent,
        ProductResponse product,
        Instant postedAt
) {
}