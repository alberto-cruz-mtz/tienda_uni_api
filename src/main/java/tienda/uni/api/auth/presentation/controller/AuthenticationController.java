package tienda.uni.api.auth.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import tienda.uni.api.auth.presentation.dto.AuthenticationRequest;
import tienda.uni.api.auth.presentation.dto.AuthenticationResponse;
import tienda.uni.api.auth.presentation.dto.RegisterRequest;
import tienda.uni.api.auth.presentation.dto.RegisterResponse;
import tienda.uni.api.auth.service.interfaces.AuthenticationService;
import tienda.uni.api.auth.service.interfaces.RefreshTokenService;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Value("${app.cookie.secure}")
    public boolean IS_COOKIE_SECURE;

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        var response = authenticationService.register(request);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", response.accessToken())
                .httpOnly(true)
                .secure(IS_COOKIE_SECURE)
                .path("/api")
                .maxAge(900) // 15 minutes
                .sameSite("Strict")
                .build();

        String refreshToken = response.refreshToken().toString();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(IS_COOKIE_SECURE)
                .path("/api/auth/refresh")
                .maxAge(604800) // 7 days
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString(), refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var response = authenticationService.authenticate(request.email(), request.password());

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", response.accessToken())
                .httpOnly(true)
                .secure(IS_COOKIE_SECURE)
                .path("/api")
                .maxAge(900) // 15 minutes
                .sameSite("Strict")
                .build();

        String refreshToken = response.refreshToken().toString();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(IS_COOKIE_SECURE)
                .path("/api/auth/refresh")
                .maxAge(604800) // 7 days
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString(), refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(@CookieValue(value = "refreshToken") UUID refreshToken) {
        var tokens = refreshTokenService.renewAccessAndRefreshToken(refreshToken);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokens.accessToken())
                .httpOnly(true)
                .secure(IS_COOKIE_SECURE)
                .path("/api")
                .maxAge(900) // 15 minutes
                .sameSite("Strict")
                .build();

        String newRefreshToken = tokens.refreshToken().toString();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(IS_COOKIE_SECURE)
                .path("/api/auth/refresh")
                .maxAge(604800) // 7 days
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString(), refreshTokenCookie.toString())
                .body(null);
    }
}
