package tienda.uni.api.app.configuration.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import tienda.uni.api.auth.service.exception.InvalidAccessTokenException;
import tienda.uni.api.auth.util.JwtUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${app.url.error}")
    public String ERROR_URL;

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Cookie tokenCookie = WebUtils.getCookie(request, "accessToken");

        if (tokenCookie != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = tokenCookie.getValue();

            try {
                DecodedJWT decodedJWT = jwtUtil.validateToken(token);

                UserDetails userDetails = jwtUtil.getUserDetailsFromToken(decodedJWT);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        token,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (InvalidAccessTokenException exception) {
                String problemDetail = """
                        {
                          "type": "%s/unauthorized",
                          "title": "Access Denied",
                          "status": 401,
                          "detail": "%s",
                          "instance": "%s"
                        }
                        """.formatted(this.ERROR_URL, exception.getMessage(), request.getRequestURI());

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(problemDetail);
            }
        }

        filterChain.doFilter(request, response);
    }
}
