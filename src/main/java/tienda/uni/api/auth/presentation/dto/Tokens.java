package tienda.uni.api.auth.presentation.dto;

import java.util.UUID;

public record Tokens(
        String accessToken,
        UUID refreshToken
) {
}
