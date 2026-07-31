package tienda.uni.api.auth.service.interfaces;

import tienda.uni.api.auth.persistence.entity.UserEntity;
import tienda.uni.api.auth.presentation.dto.Tokens;

import java.util.UUID;

public interface RefreshTokenService {

    UUID generateRefreshToken(UserEntity user);

    Tokens renewAccessAndRefreshToken(UUID refreshToken);
}
