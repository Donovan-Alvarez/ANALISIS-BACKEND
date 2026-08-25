package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.OpcionPermisoRequestDTO;
import com.analisis_sistemas.erp.dto.RoleOpcionesResponseDTO;
import com.analisis_sistemas.erp.service.RoleOpcionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles/{idRole}/opciones/por-modulo/{idModulo}")
public class RoleOpcionController {

    private final RoleOpcionService roleOpcionService;

    public RoleOpcionController(RoleOpcionService roleOpcionService) {
        this.roleOpcionService = roleOpcionService;
    }

    @GetMapping
    public ResponseEntity<RoleOpcionesResponseDTO> obtenerPorModulo(@PathVariable Integer idRole,
                                                                      @PathVariable Integer idModulo) {
        return ResponseEntity.ok(roleOpcionService.obtenerPorModulo(idRole, idModulo));
    }

    @PutMapping
    public ResponseEntity<RoleOpcionesResponseDTO> guardarPorModulo(@PathVariable Integer idRole,
                                                                      @PathVariable Integer idModulo,
                                                                      @Valid @RequestBody List<OpcionPermisoRequestDTO> permisos) {
        return ResponseEntity.ok(roleOpcionService.guardarPorModulo(idRole, idModulo, permisos));
    }
}
