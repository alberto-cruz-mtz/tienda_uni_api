package tienda.uni.api.post.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import tienda.uni.api.post.persistence.model.MediaType;

public record MediaContentRequest(
        @NotBlank(message = "La URL es obligatoria.")
        @URL(message = "La URL debe ser una URL válida y accesible públicamente.")
        String url,

        @NotNull(message = "El tipo de contenido es obligatorio.")
        MediaType type,

        @NotNull(message = "La posición es obligatoria.")
        @Min(value = 0, message = "La posición debe ser un entero mayor o igual a 0.")
        @Max(value = 9, message = "La posición debe ser un entero menor o igual a 9.")
        Integer position
) {
}