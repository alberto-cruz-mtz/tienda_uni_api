package tienda.uni.api.post.presentation.dto;

import java.util.List;

public record BatchUploadResponse(
        List<PresignedUrlItem> presignedUrls
) {
}
