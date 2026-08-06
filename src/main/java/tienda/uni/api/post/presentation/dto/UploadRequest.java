package tienda.uni.api.post.presentation.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UploadRequest(
        @NotBlank(message = "El nombre del archivo es obligatorio.")
        @Size(max = 99, message = "El nombre del archivo debe tener menos de 100 caracteres.")
        @Pattern(
                regexp = "^[A-Za-z0-9._-]+$",
                message = "El nombre del archivo no puede contener espacios, acentos, emojis ni caracteres especiales.")
        String fileName,

        @NotBlank(message = "El tipo de contenido es obligatorio.")
        @Pattern(
                regexp = "^(video/mp4|video/webm|video/quicktime|image/jpg|image/jpeg|image/gif|image/png|image/svg\\+xml|image/webp)$",
                message = "El tipo de contenido debe ser un MIME type válido (video/mp4, video/webm, video/quicktime, image/jpg, image/jpeg, image/gif, image/png, image/svg+xml, image/webp)."
        )
        String contentType,

        @NotNull(message = "El tamaño del archivo es obligatorio.")
        @Min(value = 1, message = "El tamaño del archivo debe ser mayor a 0.")
        Long fileSize
) {
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 50L * 1024 * 1024;

    @AssertTrue(message = "El tamaño del archivo debe ser menor o igual a 10 MB para imágenes y menor o igual a 50 MB para videos.")
    public boolean isFileSizeWithinAllowedLimit() {
        if (fileSize == null || contentType == null) {
            return true;
        }
        long limit = switch (contentType.toLowerCase()) {
            case "video/mp4", "video/webm", "video/quicktime" -> MAX_VIDEO_SIZE;
            case "image/jpg", "image/jpeg", "image/gif", "image/png", "image/svg+xml", "image/webp" -> MAX_IMAGE_SIZE;
            default -> -1;
        };
        if (limit < 0) {
            return true;
        }
        return fileSize <= limit;
    }
}