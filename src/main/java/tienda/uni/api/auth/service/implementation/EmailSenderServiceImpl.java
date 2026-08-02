package tienda.uni.api.auth.service.implementation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tienda.uni.api.auth.service.interfaces.EmailSenderService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender sender;
    private final String VERIFICATION_EMAIL_TEMPLATE;
    private final String senderEmail;

    public EmailSenderServiceImpl(
            JavaMailSender sender,
            @Value("classpath:templates.welcome-email.html") Resource templateResource,
            @Value("${app.mail.sender.email}") String senderEmail
    ) throws IOException {
        this.sender = sender;

        var templateData = templateResource.getInputStream().readAllBytes();
        this.VERIFICATION_EMAIL_TEMPLATE = new String(templateData, StandardCharsets.UTF_8);
        this.senderEmail = senderEmail;
    }

    @Override
    @Async("emailExecutor")
    public void sendVerificationEmail(@NonNull String email, @NonNull UUID verificationCode) {
        //TODO: Cambiar la URL de verificación a la URL de producción cuando se despliegue la aplicación
        String verificationLink = "http://localhost:3000/verify-email?token=" + verificationCode;

        String htmlContent = this.VERIFICATION_EMAIL_TEMPLATE
                .replace("{username}", email)
                .replace("{verification_link}", verificationLink);

        try {
            MimeMessage message = this.sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(email);
            helper.setFrom(this.senderEmail);
            helper.setSubject("Bienvenido a nuestra plataforma - Verifica tu correo electrónico");
            helper.setText(htmlContent, true);

            this.sender.send(message);
        } catch (MessagingException exception) {
            log.error("Error sending verification email to {}: {}", email, exception.getMessage());
        }
    }

}
