package tienda.uni.api.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tienda.uni.api.persistence.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

public record AuthenticationResponse(
        UUID id,
        UserResponse user,
        boolean isVerified,
        Instant expiresAt,

        @JsonIgnore
        String accessToken,

        @JsonIgnore
        UUID refreshToken
) {

    public static AuthenticationResponse create(UserEntity user, UserResponse userResponse, TokenBundle tokenBundle) {
        return new AuthenticationResponse(
                user.getId(),
                userResponse,
                user.isVerified(),
                tokenBundle.expirationTime(),
                tokenBundle.accessToken(),
                tokenBundle.refreshToken());
    }
}
