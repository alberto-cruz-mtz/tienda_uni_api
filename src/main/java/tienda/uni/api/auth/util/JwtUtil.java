package tienda.uni.api.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tienda.uni.api.auth.persistence.model.AuthenticatedUser;
import tienda.uni.api.auth.service.exception.InvalidAccessTokenException;

import java.time.Instant;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access-token}")
    public Long TOKEN_EXPIRATION_TIME_IN_SECONDS;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(AuthenticatedUser user) {
        Algorithm algorithm = Algorithm.HMAC512(this.secret);

        String subject = user.getUsername();
        String authorities = user.joinedAuthorities();
        String userId = user.getUser().getId().toString();
        String universityId = user.getUser().getUniversity().getId().toString();
        Instant now = Instant.now();

        return JWT.create()
                .withSubject(subject)
                .withIssuer(this.issuer)
                .withExpiresAt(now.plusSeconds(this.TOKEN_EXPIRATION_TIME_IN_SECONDS))
                .withNotBefore(now)
                .withIssuedAt(now)
                .withClaim("userId", userId)
                .withClaim("universityId", universityId)
                .withClaim("authorities", authorities)
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) throws InvalidAccessTokenException {
        Algorithm algorithm = Algorithm.HMAC512(this.secret);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(this.issuer)
                .build();

        try {
            return verifier.verify(token);
        } catch (TokenExpiredException exception) {
            throw new InvalidAccessTokenException("El token de acceso ha expirado. Solicita uno nuevo para activar tu cuenta.");
        } catch (JWTVerificationException exception) {
            throw new InvalidAccessTokenException("El token de acceso ha sido modificado o es inválido. Solicita uno nuevo para autenticarte.");
        }
    }

    public UserDetails getUserDetailsFromToken(DecodedJWT decodedJWT) {
        return AuthenticatedUser.fromToken(decodedJWT);
    }

    public static UUID getUserIdFromToken(DecodedJWT decodedJWT) {
        return UUID.fromString(decodedJWT.getClaim("userId").asString());
    }

    public static UUID getUniversityIdFromToken(DecodedJWT decodedJWT) {
        return UUID.fromString(decodedJWT.getClaim("universityId").asString());
    }
}