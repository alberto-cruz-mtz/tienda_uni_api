package tienda.uni.api.auth.presentation.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tienda.uni.api.auth.service.exception.EmailAlreadyExistsException;
import tienda.uni.api.auth.service.exception.EmailDomainNotAllowedException;

import java.net.URI;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @Value("${app.url.error}")
    private String ERROR_URL;

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "email-already-registered",
                "Email Already Registered",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(EmailDomainNotAllowedException.class)
    public ResponseEntity<ProblemDetail> handleEmailDomainNotAllowed(EmailDomainNotAllowedException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "email-domain-not-allowed",
                "Email Domain Not Allowed",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "email-not-registered",
                "Email Not Registered",
                "El correo electrónico proporcionado no está registrado.",
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "unauthorized",
                "Unauthorized",
                "Las credenciales proporcionadas no son válidas.",
                request
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ProblemDetail> handleDisabledAccount(DisabledException ex, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "account-disabled",
                "Account Disabled",
                "La cuenta del usuario está deshabilitada.",
                request
        );
    }

    private ResponseEntity<ProblemDetail> buildErrorResponse(
            HttpStatus status,
            String typeSlug,
            String title,
            String detail,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(ERROR_URL + "/" + typeSlug));
        problem.setTitle(title);

        return ResponseEntity.status(status).body(problem);
    }
}