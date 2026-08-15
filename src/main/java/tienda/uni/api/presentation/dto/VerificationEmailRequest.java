package tienda.uni.api.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VerificationEmailRequest(
        @NotNull(message = "El token es obligatorio") UUID token
) {
}
