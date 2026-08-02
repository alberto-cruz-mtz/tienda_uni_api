package tienda.uni.api.auth.presentation.dto;

public record VerificationEmailCookieResponse(
        boolean isVerified,
        String accessToken
) {
}
