package tienda.uni.api.post.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostRequest(
        @NotBlank(message = "El título es obligatorio.")
        @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres.")
        String title,

        @NotBlank(message = "La descripción es obligatoria.")
        @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres.")
        String description,

        @Valid
        @NotNull(message = "El contenido multimedia es obligatorio.")
        @Size(min = 1, max = 10, message = "El arreglo debe contener entre 1 y 10 elementos.")
        List<MediaContentRequest> mediaContent,

        @Valid
        @NotNull(message = "El producto es obligatorio.")
        ProductRequest product
) {
}