package ru.skillbox.socialnet.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String authJwt = request.getHeader("authorization");

        if (authJwt == null || !jwtTokenUtils.validateAccessToken(authJwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationContext(authJwt);
        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(String token) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        jwtTokenUtils.getSubject(token),
                        null,
                        jwtTokenUtils.getRoles(token).stream().map(SimpleGrantedAuthority::new).toList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
