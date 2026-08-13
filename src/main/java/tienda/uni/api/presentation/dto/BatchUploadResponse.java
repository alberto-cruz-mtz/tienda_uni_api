package tienda.uni.api.presentation.dto;

import java.util.List;

public record BatchUploadResponse(
        List<PresignedUrlItem> presignedUrls
) {
}
