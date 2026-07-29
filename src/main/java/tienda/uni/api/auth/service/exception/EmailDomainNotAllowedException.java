package tienda.uni.api.auth.service.exception;

public class EmailDomainNotAllowedException extends RuntimeException {
    public EmailDomainNotAllowedException(String message) {
        super(message);
    }
}
