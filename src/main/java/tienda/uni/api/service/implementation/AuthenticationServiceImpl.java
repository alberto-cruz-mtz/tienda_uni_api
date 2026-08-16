package tienda.uni.api.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.uni.api.configuration.S3Properties;
import tienda.uni.api.persistence.entity.VerificationTokenEntity;
import tienda.uni.api.persistence.model.AuthenticatedUser;
import tienda.uni.api.persistence.entity.ProfileEntity;
import tienda.uni.api.persistence.model.Role;
import tienda.uni.api.persistence.entity.RoleEntity;
import tienda.uni.api.persistence.repository.RoleRepository;
import tienda.uni.api.persistence.entity.UniversityEntity;
import tienda.uni.api.persistence.repository.UniversityRepository;
import tienda.uni.api.persistence.entity.UserEntity;
import tienda.uni.api.persistence.repository.UserRepository;
import tienda.uni.api.persistence.repository.VerificationTokenRepository;
import tienda.uni.api.presentation.dto.AuthenticationResponse;
import tienda.uni.api.presentation.dto.RegisterRequest;
import tienda.uni.api.presentation.dto.RegisterResponse;
import tienda.uni.api.presentation.dto.TokenBundle;
import tienda.uni.api.presentation.dto.UserResponse;
import tienda.uni.api.service.interfaces.AuthenticationService;
import tienda.uni.api.service.interfaces.EmailSenderService;
import tienda.uni.api.service.interfaces.RefreshTokenService;
import tienda.uni.api.service.exception.EmailAlreadyExistsException;
import tienda.uni.api.service.exception.EmailDomainNotAllowedException;
import tienda.uni.api.util.JwtUtil;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;
    private final EmailSenderService emailSender;
    private final S3Properties s3Properties;

    @Override
    @Transactional
    public AuthenticationResponse authenticate(String email, String password) {
        Authentication authentication = this.authenticateUserCredentials(email, password);
        UserEntity user = this.findUserByAuthentication(authentication);
        return this.buildAuthenticationResponse(user);
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        this.ensureThatEmailIsNotRegistered(request.email());

        UserEntity user = this.buildNewUser(request);
        this.sendVerificationEmailAsynchronously(user);

        String fullName = String.format("%s %s", request.firstName(), request.lastName());
        return this.buildRegisterResponse(fullName, user);
    }

    private Authentication authenticateUserCredentials(String email, String password) {
        Authentication credentials = new UsernamePasswordAuthenticationToken(email, password, Collections.emptyList());
        return authenticationManager.authenticate(credentials);
    }

    private UserEntity findUserByAuthentication(Authentication authentication) {
        //Principal is always AuthenticatedUser because we own the UserDetailsService.
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        assert authenticatedUser != null;

        return authenticatedUser.getUser();
    }

    private void ensureThatEmailIsNotRegistered(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("El correo electrónico proporcionado ya está registrado.");
        }
    }

    private UniversityEntity findUniversityByEmail(String email) {
        String domain = email.split("@")[1];
        String errorMessage = "El dominio del correo electrónico proporcionado no está permitido o registrado.";

        return universityRepository.findBySpecificDomain(domain)
                .orElseThrow(() -> new EmailDomainNotAllowedException(errorMessage));
    }

    private RoleEntity getUnverifiedUserRole() {
        RoleEntity role = roleRepository.findByName(Role.UNVERIFIED);
        assert role != null; // This assertion is safe because the role is predefined and should always exist in the database.
        return role;
    }

    private UserEntity buildNewUser(RegisterRequest request) {
        UniversityEntity university = this.findUniversityByEmail(request.email());
        RoleEntity role = this.getUnverifiedUserRole();

        String encodedPassword = passwordEncoder.encode(request.password());
        var profile = ProfileEntity.create(request.firstName(), request.lastName());

        return this.assembleUser(request.email(), encodedPassword, university, role, profile);
    }

    private UserEntity assembleUser(String email, String password, UniversityEntity university, RoleEntity role, ProfileEntity profile) {
        UserEntity user = UserEntity.create(email, password, Set.of(role), profile, university);
        profile.setUser(user); // bidirectional ownership; ProfileEntity cannot set itself without User identity
        return this.userRepository.save(user);
    }

    private void sendVerificationEmailAsynchronously(UserEntity user) {
        var verificationToken = VerificationTokenEntity.create(user);
        verificationTokenRepository.save(verificationToken);

        emailSender.sendVerificationEmail(user.getEmail(), verificationToken.getToken());
    }

    private TokenBundle buildTokensFor(UserEntity user) {
        AuthenticatedUser authenticatedUser = AuthenticatedUser.fromUserEntity(user);

        String accessToken = jwtUtil.generateToken(authenticatedUser);
        UUID refreshToken = refreshTokenService.generateRefreshToken(user);
        Instant expirationTime = Instant.now().plusSeconds(jwtUtil.TOKEN_EXPIRATION_TIME_IN_SECONDS);

        return new TokenBundle(accessToken, refreshToken, expirationTime);
    }

    private AuthenticationResponse buildAuthenticationResponse(UserEntity user) {
        var userResponse = UserResponse.createResponseForAuthentication(user.getProfile(), s3Properties.buckets().profilePictures().url());
        var tokenBundle = this.buildTokensFor(user);
        return AuthenticationResponse.create(user, userResponse, tokenBundle);
    }

    private RegisterResponse buildRegisterResponse(String fullName, UserEntity user) {
        UserResponse userResponse = UserResponse.createResponseForRegistration(user.getEmail(), fullName);
        TokenBundle tokenBundle = this.buildTokensFor(user);
        return RegisterResponse.create(user, userResponse, tokenBundle);
    }

}