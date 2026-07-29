package tienda.uni.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tienda.uni.api.auth.persistence.entity.RefreshTokenEntity;
import tienda.uni.api.auth.persistence.repository.RefreshTokenRepository;
import tienda.uni.api.auth.persistence.entity.UserEntity;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.expiration.refresh-token}")
    public Long REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS;

    private final RefreshTokenRepository repository;

    @Override
    public UUID generateRefreshToken(UserEntity user) {
        var refreshToken = this.buildRefreshToken(user);
        var savedRefreshToken = repository.save(refreshToken);

        return savedRefreshToken.getToken();
    }

    public RefreshTokenEntity buildRefreshToken(UserEntity user) {
        UUID token = UUID.randomUUID();
        Instant expiresAt = Instant.now().plusSeconds(this.REFRESH_TOKEN_EXPIRATION_TIME_IN_SECONDS);

        return RefreshTokenEntity.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .build();
    }
}
