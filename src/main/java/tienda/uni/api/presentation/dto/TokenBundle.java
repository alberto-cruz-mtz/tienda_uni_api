package tienda.uni.api.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record TokenBundle(String accessToken, UUID refreshToken, Instant expirationTime) {
}
