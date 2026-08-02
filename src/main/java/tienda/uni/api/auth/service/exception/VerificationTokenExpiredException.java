package tienda.uni.api.auth.service.exception;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException(String message) {
        super(message);
    }
}
