package tienda.uni.api.auth.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tienda.uni.api.auth.presentation.dto.VerificationEmailRequest;
import tienda.uni.api.auth.service.interfaces.VerificationEmailService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class VerificationEmailController {

    @Value("${app.cookie.secure}")
    private boolean IS_COOKIE_SECURE;

    private final VerificationEmailService verificationEmailService;

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerificationEmailRequest request) {
        verificationEmailService.verifyEmail(request.token());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> requestOtherVerificationEmail(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        verificationEmailService.requestNewVerificationEmail(email);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email-status")
    public ResponseEntity<Void> verifyIfEmailAlreadyVerified(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        var isVerified = verificationEmailService.isEmailVerified(email);

        HttpStatus status = isVerified ? HttpStatus.OK : HttpStatus.ACCEPTED;

        return ResponseEntity.status(status).build();
    }
}
