package tienda.uni.api.presentation.dto;

public record PresignedUrlItem(
        String fileId,
        String uploadUrl,
        String fileKey
) {
}
