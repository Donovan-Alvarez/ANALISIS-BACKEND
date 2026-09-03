package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.ModuloMenuDTO;
import com.analisis_sistemas.erp.service.MenuUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuUsuarioController {

    private final MenuUsuarioService menuUsuarioService;

    public MenuUsuarioController(MenuUsuarioService menuUsuarioService) {
        this.menuUsuarioService = menuUsuarioService;
    }

    @GetMapping("/actual")
    public ResponseEntity<List<ModuloMenuDTO>> obtenerMenuActual(Authentication authentication) {
        String idUsuario = authentication.getName();
        return ResponseEntity.ok(menuUsuarioService.obtenerMenuDelUsuario(idUsuario));
    }
}
