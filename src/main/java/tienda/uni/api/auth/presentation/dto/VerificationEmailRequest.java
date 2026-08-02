package tienda.uni.api.auth.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VerificationEmailRequest(
        @NotNull(message = "El token es obligatorio") UUID token
) {
}
