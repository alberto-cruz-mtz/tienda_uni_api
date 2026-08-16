package tienda.uni.api.presentation.controller;

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
import tienda.uni.api.presentation.dto.AuthenticationRequest;
import tienda.uni.api.presentation.dto.AuthenticationResponse;
import tienda.uni.api.presentation.dto.RegisterRequest;
import tienda.uni.api.presentation.dto.RegisterResponse;
import tienda.uni.api.service.interfaces.AuthenticationService;
import tienda.uni.api.service.interfaces.RefreshTokenService;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Value("${app.cookie.secure}")
    private boolean COOKIE_SECURE;

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    private static final long SEVEN_DAYS_IN_SECONDS = 604800;
    private static final long FIFTEEN_MINUTES_IN_SECONDS = 900;
    private static final long ZERO_SECONDS = 0;
    private static final String EMPTY_TOKEN = "";

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        var response = authenticationService.register(request);
        var cookieHeader = this.generateTokenCookieHeader(response.accessToken(), response.refreshToken().toString());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(cookieHeader)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var response = authenticationService.authenticate(request.email(), request.password());
        var cookieHeader = this.generateTokenCookieHeader(response.accessToken(), response.refreshToken().toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(cookieHeader)
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(@CookieValue(value = "refreshToken") UUID refreshToken) {
        var tokens = refreshTokenService.renewAccessAndRefreshToken(refreshToken);
        var cookieHeader = this.generateTokenCookieHeader(tokens.accessToken(), tokens.refreshToken().toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(cookieHeader)
                .body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) UUID refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
        var cookieHeader = this.generateCookieHeaderCleanup();

        return ResponseEntity
                .noContent()
                .headers(cookieHeader)
                .build();
    }

    private HttpHeaders generateTokenCookieHeader(String accessToken, String refreshToken) {
        var accessTokenCookie = this.buildAccessTokenCookie(accessToken, FIFTEEN_MINUTES_IN_SECONDS);
        var refreshTokenCookie = this.buildRefreshTokenCookie(refreshToken, SEVEN_DAYS_IN_SECONDS);

        return this.buildCookieHeaderWithTokens(accessTokenCookie, refreshTokenCookie);
    }

    private HttpHeaders generateCookieHeaderCleanup() {
        var accessTokenCookie = this.buildAccessTokenCookie(EMPTY_TOKEN, ZERO_SECONDS);
        var refreshTokenCookie = this.buildRefreshTokenCookie(EMPTY_TOKEN, ZERO_SECONDS);

        return this.buildCookieHeaderWithTokens(accessTokenCookie, refreshTokenCookie);
    }

    private HttpHeaders buildCookieHeaderWithTokens(ResponseCookie accessTokenCookie, ResponseCookie refreshTokenCookie) {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        return headers;
    }

    private ResponseCookie buildAccessTokenCookie(String accessToken, long expirationTimeInSeconds) {
        return this.createCookie("accessToken", accessToken, "/api", expirationTimeInSeconds);
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken, long expirationTimeInSeconds) {
        return this.createCookie("refreshToken", refreshToken, "/api/auth", expirationTimeInSeconds);
    }

    private ResponseCookie createCookie(String name, String value, String path, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .path(path)
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }
}