package tienda.uni.api.service.exception;

public class EmailDomainNotAllowedException extends RuntimeException {
    public EmailDomainNotAllowedException(String message) {
        super(message);
    }
}
