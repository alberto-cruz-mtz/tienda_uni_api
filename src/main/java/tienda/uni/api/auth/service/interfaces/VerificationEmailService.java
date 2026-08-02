package tienda.uni.api.auth.service.interfaces;

import tienda.uni.api.auth.presentation.dto.VerificationEmailCookieResponse;

import java.util.UUID;

public interface VerificationEmailService {

    void verifyEmail(UUID token);

    void requestNewVerificationEmail(String email);

    VerificationEmailCookieResponse isEmailVerified(String email);
}
