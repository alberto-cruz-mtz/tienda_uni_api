package tienda.uni.api.auth.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tienda.uni.api.auth.persistence.entity.RefreshTokenEntity;
import tienda.uni.api.auth.persistence.model.AuthenticatedUser;
import tienda.uni.api.auth.persistence.repository.RefreshTokenRepository;
import tienda.uni.api.auth.persistence.entity.UserEntity;
import tienda.uni.api.auth.presentation.dto.Tokens;
import tienda.uni.api.auth.service.interfaces.RefreshTokenService;
import tienda.uni.api.auth.service.exception.RefreshTokenExpiredException;
import tienda.uni.api.auth.service.exception.RefreshTokenMissingException;
import tienda.uni.api.auth.service.exception.RefreshTokenNotFoundException;
import tienda.uni.api.auth.service.exception.RefreshTokenRevokedException;
import tienda.uni.api.auth.util.JwtUtil;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.expiration.refresh-token}")
    public Long REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS;

    private final RefreshTokenRepository repository;
    private final JwtUtil jwtUtil;

    @Override
    public UUID generateRefreshToken(UserEntity user) {
        var refreshToken = this.buildRefreshToken(user);
        var savedRefreshToken = repository.save(refreshToken);

        return savedRefreshToken.getToken();
    }

    @Override
    public Tokens renewAccessAndRefreshToken(UUID refreshToken) {
        if (refreshToken == null) {
            throw new RefreshTokenMissingException("No se proporcionó el refresh token. Asegúrate de que la cookie esté presente.");
        }

        var refreshTokenEntity = repository.findByToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenNotFoundException(
                        "El Refresh Token proporcionado no esta registrado. Por favor vuelve a iniciar sesión para obtener un nuevo Refresh Token."));

        if (refreshTokenEntity.isRevoked()) {
            throw new RefreshTokenRevokedException("El Refresh Token proporcionado ha sido revocado. Por favor vuelve a iniciar sesión para obtener un nuevo Refresh Token.");
        }

        if (refreshTokenEntity.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenExpiredException("El Refresh Token proporcionado ha expirado. Por favor vuelve a iniciar sesión para obtener un nuevo Refresh Token.");
        }

        UserEntity user = refreshTokenEntity.getUser();
        AuthenticatedUser authenticatedUser = AuthenticatedUser.fromUserEntity(user);

        String accessToken = jwtUtil.generateToken(authenticatedUser);
        refreshTokenEntity.setToken(UUID.randomUUID());
        refreshTokenEntity.setExpiresAt(Instant.now().plusSeconds(this.REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS));
        RefreshTokenEntity updatedRefreshToken = repository.save(refreshTokenEntity);

        return new Tokens(accessToken, updatedRefreshToken.getToken());
    }

    private RefreshTokenEntity buildRefreshToken(UserEntity user) {
        UUID token = UUID.randomUUID();
        Instant expiresAt = Instant.now().plusSeconds(this.REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS);

        return RefreshTokenEntity.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .build();
    }
}
