package tienda.uni.api.service.interfaces;

import java.util.UUID;

public interface EmailSenderService {

    void sendVerificationEmail(String email, UUID verificationCode);
}
