package tienda.uni.api.auth.service.interfaces;

import tienda.uni.api.auth.presentation.dto.AuthenticationResponse;
import tienda.uni.api.auth.presentation.dto.RegisterRequest;
import tienda.uni.api.auth.presentation.dto.RegisterResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate(String email, String password);

    RegisterResponse register(RegisterRequest request);
}
