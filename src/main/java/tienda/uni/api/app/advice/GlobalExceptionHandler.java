package tienda.uni.api.app.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.url.error}")
    private String ERROR_URL;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        Map<String, IncorrectField> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new IncorrectField(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toMap(IncorrectField::field, Function.identity(), (existing, replacement) -> existing));

        String message = "Uno o más campos no cumplen con las reglas de validación.";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setType(URI.create(ERROR_URL + "/validations"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("errors", fieldErrors);

        return ResponseEntity.status(status).body(problemDetail);
    }

}