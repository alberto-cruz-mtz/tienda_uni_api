package tienda.uni.api.auth.service.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}
