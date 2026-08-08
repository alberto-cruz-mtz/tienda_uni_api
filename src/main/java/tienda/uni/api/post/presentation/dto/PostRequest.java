package tienda.uni.api.post.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tienda.uni.api.post.persistence.model.TagName;

import java.time.Instant;
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

        @NotNull(message = "El arreglo de etiquetas es obligatorio.")
        @Size(min = 1, max = 5, message = "El arreglo de etiquetas debe contener entre 1 y 5 elementos.")
        List<TagName> tags,

        @Valid
        @NotNull(message = "El producto es obligatorio.")
        ProductRequest product,

        @Future(message = "La fecha de ocultación debe ser una fecha futura.")
        Instant hiddenUntil,

        @NotNull(message = "El campo publishRightAway es obligatorio.")
        Boolean publishRightAway
) {
}