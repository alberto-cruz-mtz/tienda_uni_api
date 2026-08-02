package tienda.uni.api.auth.presentation.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tienda.uni.api.app.dto.ProblemDetailResponse;
import tienda.uni.api.auth.service.exception.UserAlreadyVerifiedException;
import tienda.uni.api.auth.service.exception.VerificationTokenExpiredException;
import tienda.uni.api.auth.service.exception.VerificationTokenNotFoundException;

@RestControllerAdvice
public class VerificationEmailExceptionHandler {

    @Value("${app.url.error}")
    private String ERROR_URL;

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleVerificationTokenNotFound(VerificationTokenNotFoundException exception) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.NOT_FOUND,
                ERROR_URL + "/verification-token-not-found",
                "Verification Token Not Found",
                exception.getMessage()
        );
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ProblemDetail> handleVerificationTokenExpired(VerificationTokenExpiredException exception) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.GONE,
                ERROR_URL + "/verification-token-expired",
                "Verification Token Expired",
                exception.getMessage()
        );
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyVerified(UserAlreadyVerifiedException exception) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.CONFLICT,
                ERROR_URL + "/user-already-verified",
                "User Already Verified",
                exception.getMessage()
        );
    }
}
