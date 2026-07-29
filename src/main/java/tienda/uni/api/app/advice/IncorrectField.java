package tienda.uni.api.app.advice;

public record IncorrectField(
        String field,
        String message
) {
}
