package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.LoginRequestDTO;
import com.analisis_sistemas.erp.dto.LoginResponseDTO;
import com.analisis_sistemas.erp.security.JwtService;
import com.analisis_sistemas.erp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_BEARER = "Bearer ";

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        String httpUserAgent = request.getHeader("User-Agent");
        String direccionIp = request.getRemoteAddr();

        LoginResponseDTO response = authService.login(dto, httpUserAgent, direccionIp);
        return ResponseEntity.ok(response);
    }

    // Requiere JWT valido (no esta en la lista de rutas publicas de SecurityConfig).
    // El idSesion se saca del propio token, igual que hace JwtAuthenticationFilter.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header != null && header.startsWith(PREFIX_BEARER)) {
            String token = header.substring(PREFIX_BEARER.length());
            String idSesion = jwtService.getIdSesionFromToken(token);
            authService.logout(idSesion);
        }

        return ResponseEntity.noContent().build();
    }
}
