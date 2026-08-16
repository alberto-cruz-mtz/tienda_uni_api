package tienda.uni.api.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tienda.uni.api.persistence.entity.UserEntity;

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

    public static RegisterResponse create(UserEntity user, UserResponse userResponse, TokenBundle tokenBundle) {
        return new RegisterResponse(
                user.getId(),
                userResponse,
                user.isVerified(),
                tokenBundle.expirationTime(),
                tokenBundle.accessToken(),
                tokenBundle.refreshToken()
        );
    }
}