package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.LoginRequestDTO;
import com.analisis_sistemas.erp.dto.LoginResponseDTO;
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

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        String httpUserAgent = request.getHeader("User-Agent");
        String direccionIp = request.getRemoteAddr();

        LoginResponseDTO response = authService.login(dto, httpUserAgent, direccionIp);
        return ResponseEntity.ok(response);
    }
}
