package tienda.uni.api.service.interfaces;

import tienda.uni.api.presentation.dto.AuthenticationResponse;
import tienda.uni.api.presentation.dto.RegisterRequest;
import tienda.uni.api.presentation.dto.RegisterResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate(String email, String password);

    RegisterResponse register(RegisterRequest request);
}
