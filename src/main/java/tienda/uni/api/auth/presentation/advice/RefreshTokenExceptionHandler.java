package tienda.uni.api.auth.presentation.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tienda.uni.api.app.dto.ProblemDetailResponse;
import tienda.uni.api.auth.service.exception.RefreshTokenExpiredException;
import tienda.uni.api.auth.service.exception.RefreshTokenMissingException;
import tienda.uni.api.auth.service.exception.RefreshTokenNotFoundException;
import tienda.uni.api.auth.service.exception.RefreshTokenRevokedException;

@RestControllerAdvice
public class RefreshTokenExceptionHandler {

    @Value("${app.url.error}")
    private String ERROR_URL;

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleRefreshTokenNotFound(RefreshTokenNotFoundException exception) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.NOT_FOUND,
                ERROR_URL + "/not-found",
                "Refresh Token Not Found",
                exception.getMessage()
        );
    }

    @ExceptionHandler(RefreshTokenRevokedException.class)
    public ResponseEntity<ProblemDetail> handleRefreshTokenRevoked(RefreshTokenRevokedException exception) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_URL + "/refresh-token-invalid",
                "Refresh Token Revoked",
                exception.getMessage()
        );
    }

    @ExceptionHandler(RefreshTokenMissingException.class)
    public ResponseEntity<ProblemDetail> handleRefreshTokenMissing(RefreshTokenMissingException exception) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_URL + "/refresh-token-missing",
                "Refresh Token Missing",
                exception.getMessage()
        );
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ProblemDetail> handleRefreshTokenExpired(RefreshTokenExpiredException exception) {
        return ProblemDetailResponse.buildResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_URL + "/refresh-token-invalid",
                "Refresh Token Expired",
                exception.getMessage()
        );
    }
}
