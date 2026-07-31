package tienda.uni.api.auth.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.uni.api.auth.persistence.model.AuthenticatedUser;
import tienda.uni.api.auth.persistence.entity.ProfileEntity;
import tienda.uni.api.auth.persistence.model.Role;
import tienda.uni.api.auth.persistence.entity.RoleEntity;
import tienda.uni.api.auth.persistence.repository.RoleRepository;
import tienda.uni.api.auth.persistence.entity.UniversityEntity;
import tienda.uni.api.auth.persistence.repository.UniversityRepository;
import tienda.uni.api.auth.persistence.entity.UserEntity;
import tienda.uni.api.auth.persistence.repository.UserRepository;
import tienda.uni.api.auth.presentation.dto.AuthenticationResponse;
import tienda.uni.api.auth.presentation.dto.RegisterRequest;
import tienda.uni.api.auth.presentation.dto.RegisterResponse;
import tienda.uni.api.auth.presentation.dto.UserResponse;
import tienda.uni.api.auth.service.interfaces.AuthenticationService;
import tienda.uni.api.auth.service.interfaces.RefreshTokenService;
import tienda.uni.api.auth.service.exception.EmailAlreadyExistsException;
import tienda.uni.api.auth.service.exception.EmailDomainNotAllowedException;
import tienda.uni.api.auth.util.JwtUtil;

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

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthenticationResponse authenticate(String email, String password) {
        // authenticate user credentials
        Authentication credentials = new UsernamePasswordAuthenticationToken(email, password, Collections.emptyList());
        Authentication authentication = authenticationManager.authenticate(credentials);

        //generate response
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        assert authenticatedUser != null;

        UserEntity user = authenticatedUser.getUser();

        var userResponse = UserResponse.forAuthentication(user.getProfile());
        Instant expirationTime = Instant.now().plusSeconds(jwtUtil.TOKEN_EXPIRATION_TIME_IN_SECONDS);

        String secret = jwtUtil.generateToken(authenticatedUser);
        UUID refreshToken = refreshTokenService.generateRefreshToken(user);

        return new AuthenticationResponse(user.getId(), userResponse, user.isVerified(), expirationTime, secret, refreshToken);
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.email();

        // ensure that the email is not already registered
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("El correo electrónico proporcionado ya está registrado.");
        }

        // ensure that the email domain is allowed and registered in the system and get the corresponding university
        String domain = email.split("@")[1];
        UniversityEntity university = universityRepository.findBySpecificDomain(domain)
                .orElseThrow(() -> new EmailDomainNotAllowedException("El dominio del correo electrónico proporcionado no está permitido o registrado."));

        // get initial Role (UNVERIFIED) for the new user
        RoleEntity role = roleRepository.findByName(Role.UNVERIFIED);
        assert role != null; // This assertion is safe because the role is predefined and should always exist in the database.
        // save the new user with the provided information in the database
        String encodedPassword = passwordEncoder.encode(request.password());
        var profile = ProfileEntity.create(request.firstName(), request.lastName());
        UserEntity user = UserEntity.create(email, encodedPassword, Set.of(role), profile, university);
        profile.setUser(user); // Set the user reference in the profile entity
        UserEntity savedUser = userRepository.save(user);

        // generate response
        String fullName = request.firstName() + " " + request.lastName();
        UserResponse userResponse = UserResponse.forRegistration(email, fullName);
        Instant expirationTime = Instant.now().plusSeconds(jwtUtil.TOKEN_EXPIRATION_TIME_IN_SECONDS);

        AuthenticatedUser authenticatedUser = AuthenticatedUser.fromUserEntity(savedUser);
        String secret = jwtUtil.generateToken(authenticatedUser);

        UUID refreshToken = refreshTokenService.generateRefreshToken(savedUser);

        return new RegisterResponse(
                savedUser.getId(),
                userResponse,
                savedUser.isVerified(),
                expirationTime,
                secret,
                refreshToken
        );
    }
}
