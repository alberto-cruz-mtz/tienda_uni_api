package tienda.uni.api.auth.persistence.model;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tienda.uni.api.auth.persistence.entity.UserEntity;
import tienda.uni.api.auth.util.JwtUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUser implements UserDetails {

    private String username;
    private String password;
    private boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    private UUID userId;
    private UUID universityId;

    private UserEntity user;

    public static AuthenticatedUser fromUserEntity(UserEntity user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(3);
        user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().authority()))
                .forEach(authorities::add);

        user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName().name()))
                .forEach(authorities::add);

        return AuthenticatedUser.builder()
                .userId(user.getId())
                .universityId(user.getUniversity().getId())
                .username(user.getEmail())
                .password(user.getPassword())
                .enabled(true)
                .authorities(authorities)
                .user(user)
                .build();
    }

    public static AuthenticatedUser fromToken(DecodedJWT decodedJWT) {
        String username = decodedJWT.getSubject();

        String authoritiesString = decodedJWT.getClaim("authorities").asString();
        var authorities = Arrays.stream(authoritiesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UUID userId = JwtUtil.getUserIdFromToken(decodedJWT);
        UUID universityId = JwtUtil.getUniversityIdFromToken(decodedJWT);
        UserEntity user = UserEntity.builder()
                .id(userId)
                .build();

        return AuthenticatedUser.builder()
                .username(username)
                .authorities(authorities)
                .userId(user.getId())
                .universityId(universityId)
                .user(user)
                .build();
    }

    public String joinedAuthorities() {
        return this.authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}