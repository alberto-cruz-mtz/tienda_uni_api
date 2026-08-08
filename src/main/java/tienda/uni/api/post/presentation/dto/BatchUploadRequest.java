package tienda.uni.api.post.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BatchUploadRequest(
        @Valid
        @Size(min = 1, max = 10, message = "La lista de archivos debe contener entre 1 y 10 elementos.")
        @NotNull(message = "La lista de archivos es obligatoria.")
        List<FileMetadata> files
) {
}
