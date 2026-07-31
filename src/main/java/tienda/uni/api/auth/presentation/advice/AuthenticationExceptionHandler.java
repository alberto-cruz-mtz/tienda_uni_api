package tienda.uni.api.auth.presentation.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tienda.uni.api.app.dto.ProblemDetailResponse;
import tienda.uni.api.auth.service.exception.EmailAlreadyExistsException;
import tienda.uni.api.auth.service.exception.EmailDomainNotAllowedException;

import java.net.URI;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @Value("${app.url.error}")
    private String ERROR_URL;

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.CONFLICT,
                ERROR_URL + "/email-already-registered",
                "Email Already Registered",
                ex.getMessage()
        );
    }

    @ExceptionHandler(EmailDomainNotAllowedException.class)
    public ResponseEntity<ProblemDetail> handleEmailDomainNotAllowed(EmailDomainNotAllowedException ex) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.FORBIDDEN,
                ERROR_URL + "/email-domain-not-allowed",
                "Email Domain Not Allowed",
                ex.getMessage()
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUsernameNotFound() {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.NOT_FOUND,
                ERROR_URL + "/email-not-registered",
                "Email Not Registered",
                "El correo electrónico proporcionado no está registrado."
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials() {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_URL + "/unauthorized",
                "Unauthorized",
                "Las credenciales proporcionadas no son válidas."
        );
    }
}