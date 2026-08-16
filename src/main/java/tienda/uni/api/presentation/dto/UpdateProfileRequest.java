package tienda.uni.api.presentation.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequest(
        @Pattern(
                regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[\\w.-]+\\.(jpeg|png|jpg|webp|svg|gif)$",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "La key tiene un formato inválido. Debe comenzar con un UUID válido seguido del nombre del archivo y una extensión permitida."
        )
        String photoUrl,

        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "El ID del edificio debe ser un UUID válido")
        String buildingId
) {
}
