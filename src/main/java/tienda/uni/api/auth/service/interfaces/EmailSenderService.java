package tienda.uni.api.auth.service.interfaces;

public interface EmailSenderService {

    void sendVerificationEmail(String email, String verificationCode);
}
