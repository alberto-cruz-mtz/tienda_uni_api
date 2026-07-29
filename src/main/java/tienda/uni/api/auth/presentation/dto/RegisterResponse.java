package tienda.uni.api.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.UUID;

public record RegisterResponse(
        UUID id,
        UserResponse user,
        boolean isVerified,
        Instant expiresAt,

        @JsonIgnore
        String accessToken,

        @JsonIgnore
        UUID refreshToken
) {
}
