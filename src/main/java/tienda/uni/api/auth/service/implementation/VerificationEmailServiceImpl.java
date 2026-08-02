package tienda.uni.api.auth.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.uni.api.auth.persistence.entity.UserEntity;
import tienda.uni.api.auth.persistence.entity.VerificationTokenEntity;
import tienda.uni.api.auth.persistence.model.AuthenticatedUser;
import tienda.uni.api.auth.persistence.model.Role;
import tienda.uni.api.auth.persistence.repository.RoleRepository;
import tienda.uni.api.auth.persistence.repository.UserRepository;
import tienda.uni.api.auth.persistence.repository.VerificationTokenRepository;
import tienda.uni.api.auth.presentation.dto.VerificationEmailCookieResponse;
import tienda.uni.api.auth.service.exception.UserAlreadyVerifiedException;
import tienda.uni.api.auth.service.exception.VerificationTokenExpiredException;
import tienda.uni.api.auth.service.exception.VerificationTokenNotFoundException;
import tienda.uni.api.auth.service.interfaces.EmailSenderService;
import tienda.uni.api.auth.service.interfaces.VerificationEmailService;
import tienda.uni.api.auth.util.JwtUtil;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationEmailServiceImpl implements VerificationEmailService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void verifyEmail(UUID token) {
        var verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationTokenNotFoundException(
                        "El token de verificación proporcionado no es válido o ya no existe."));

        if (verificationToken.isExpired()) {
            throw new VerificationTokenExpiredException(
                    "El token de verificación ha expirado. Por favor, solicita un nuevo correo de verificación.");
        }

        var user = verificationToken.getUser();

        if (user.isVerified()) {
            throw new UserAlreadyVerifiedException(
                    "El correo electrónico ya fue verificado anteriormente, no es necesario volver a verificarlo.");
        }

        user.setVerified(true);
        var role = roleRepository.findByName(Role.CUSTOMER);
        user.setRoles(Set.of(role));

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void requestNewVerificationEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró un usuario con el correo electrónico proporcionado."));

        if (user.isVerified()) {
            throw new UserAlreadyVerifiedException(
                    "El correo electrónico ya fue verificado anteriormente, no es necesario volver a verificarlo.");
        }

        var verificationToken = verificationTokenRepository.findById(user.getId())
                .orElseGet(() -> VerificationTokenEntity.create(user));

        if (verificationToken.isExpired() && verificationToken.hasAtLeastFiveMinutesOfLife()) {
            verificationToken.resetExpiresAt();
            verificationToken.setToken(UUID.randomUUID());
            verificationToken = verificationTokenRepository.save(verificationToken);
        }

        emailSenderService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());
    }

    @Override
    public VerificationEmailCookieResponse isEmailVerified(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró un usuario con el correo electrónico proporcionado."));

        if (user.isVerified()) {
            var userDetails = AuthenticatedUser.fromUserEntity(user);
            String token = jwtUtil.generateToken(userDetails);
            return new VerificationEmailCookieResponse(true, token);
        }

        return new VerificationEmailCookieResponse(false, null);
    }
}
