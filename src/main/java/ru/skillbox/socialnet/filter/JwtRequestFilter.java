package ru.skillbox.socialnet.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.skillbox.socialnet.util.JwtTokenUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final SecurityContext SECURITY_CONTEXT = SecurityContextHolder.getContext();

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authJwt = request.getHeader("Authorization");
        String username = null;

        if (authJwt != null) {
            try {
                username = jwtTokenUtils.getUsername(authJwt);
            } catch (ExpiredJwtException ex) {
                log.debug("Token is expired");
            } catch (SignatureException ex) {
                log.debug("Signature is incorrect");
            }
        }

        if (username != null && SECURITY_CONTEXT.getAuthentication() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtTokenUtils.getRoles(authJwt).stream().map(SimpleGrantedAuthority::new).toList()
            );
            SECURITY_CONTEXT.setAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }
}
