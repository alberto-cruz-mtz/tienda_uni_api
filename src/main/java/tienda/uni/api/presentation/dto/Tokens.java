package tienda.uni.api.presentation.dto;

import java.util.UUID;

public record Tokens(
        String accessToken,
        UUID refreshToken
) {
}
