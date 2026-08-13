package tienda.uni.api.presentation.advice;

public record IncorrectField(
        String field,
        String message
) {
}
