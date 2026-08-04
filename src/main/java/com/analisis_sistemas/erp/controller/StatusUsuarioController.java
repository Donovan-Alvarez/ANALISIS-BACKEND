package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.StatusUsuarioDTO;
import com.analisis_sistemas.erp.service.StatusUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/status-usuario")
public class StatusUsuarioController {

    private final StatusUsuarioService statusUsuarioService;

    public StatusUsuarioController(StatusUsuarioService statusUsuarioService) {
        this.statusUsuarioService = statusUsuarioService;
    }

    @GetMapping
    public ResponseEntity<List<StatusUsuarioDTO>> findAll() {
        return ResponseEntity.ok(statusUsuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusUsuarioDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(statusUsuarioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<StatusUsuarioDTO> create(@Valid @RequestBody StatusUsuarioDTO dto) {
        StatusUsuarioDTO creado = statusUsuarioService.create(dto);
        return ResponseEntity.created(URI.create("/api/status-usuario/" + creado.getIdStatusUsuario())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusUsuarioDTO> update(@PathVariable Integer id, @Valid @RequestBody StatusUsuarioDTO dto) {
        return ResponseEntity.ok(statusUsuarioService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        statusUsuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
