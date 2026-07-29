package tienda.uni.api.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(
        @Email(message = "El formato del correo electrónico es invalido")
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Size(min = 6, max = 120, message = "El correo electrónico debe tener entre 6 y 120 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 25, message = "La contraseña debe tener entre 8 y 25 caracteres")
        String password
) {
}
