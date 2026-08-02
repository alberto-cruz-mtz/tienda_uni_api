package tienda.uni.api.auth.service.interfaces;

import java.util.UUID;

public interface VerificationEmailService {

    void verifyEmail(UUID token);

    void requestNewVerificationEmail(String email);

    boolean isEmailVerified(String email);
}
