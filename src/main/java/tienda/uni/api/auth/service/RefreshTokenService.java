package tienda.uni.api.auth.service;

import tienda.uni.api.auth.persistence.entity.UserEntity;

import java.util.UUID;

public interface RefreshTokenService {

    UUID generateRefreshToken(UserEntity user);
}
