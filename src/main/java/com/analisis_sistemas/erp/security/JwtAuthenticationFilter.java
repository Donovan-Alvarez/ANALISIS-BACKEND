package com.analisis_sistemas.erp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header != null && header.startsWith(PREFIX_BEARER)) {
            String token = header.substring(PREFIX_BEARER.length());

            if (jwtService.validateToken(token)) {
                String idUsuario = jwtService.getIdUsuarioFromToken(token);
                String nombreRole = jwtService.getNombreRoleFromToken(token);

                if (nombreRole != null) {
                    String nombreAuthority = nombreRole.toUpperCase().replace(" ", "_");
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + nombreAuthority);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(idUsuario, null, List.of(authority));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
