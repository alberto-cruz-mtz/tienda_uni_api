package tienda.uni.api.post.presentation.dto;

public record PresignedUrlItem(
        String fileId,
        String uploadUrl,
        String fileKey
) {
}
