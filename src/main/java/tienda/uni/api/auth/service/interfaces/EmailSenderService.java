package tienda.uni.api.auth.service.interfaces;

import java.util.UUID;

public interface EmailSenderService {

    void sendVerificationEmail(String email, UUID verificationCode);
}
