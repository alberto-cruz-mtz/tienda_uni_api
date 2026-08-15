package tienda.uni.api.service.interfaces;

import tienda.uni.api.persistence.entity.UserEntity;
import tienda.uni.api.presentation.dto.Tokens;

import java.util.UUID;

public interface RefreshTokenService {

    UUID generateRefreshToken(UserEntity user);

    Tokens renewAccessAndRefreshToken(UUID refreshToken);

    void revokeRefreshToken(UUID refreshToken);
}
