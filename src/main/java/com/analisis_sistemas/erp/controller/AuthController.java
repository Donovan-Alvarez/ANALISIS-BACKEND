package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.CambiarPasswordPropioRequestDTO;
import com.analisis_sistemas.erp.dto.LoginRequestDTO;
import com.analisis_sistemas.erp.dto.LoginResponseDTO;
import com.analisis_sistemas.erp.security.JwtService;
import com.analisis_sistemas.erp.service.AuthService;
import com.analisis_sistemas.erp.service.UsuarioService;
import com.analisis_sistemas.erp.utils.SecurityUtils;
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
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, JwtService jwtService, UsuarioService usuarioService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        String httpUserAgent = request.getHeader("User-Agent");
        String direccionIp = request.getRemoteAddr();

        LoginResponseDTO response = authService.login(dto, httpUserAgent, direccionIp);
        return ResponseEntity.ok(response);
    }

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

    /**
     * Cambio de password del propio usuario autenticado. Cubre tanto el
     * cambio obligatorio (RequiereCambiarPassword = 1) como un cambio
     * voluntario; en ambos casos requiere estar logueado (JWT valido).
     */
    @PostMapping("/cambiar-password")
    public ResponseEntity<Void> cambiarPasswordPropio(@Valid @RequestBody CambiarPasswordPropioRequestDTO dto) {
        String idUsuario = SecurityUtils.getUsuarioAutenticado();
        usuarioService.cambiarPasswordPropio(idUsuario, dto.getPasswordNuevo());
        return ResponseEntity.noContent().build();
    }
}
