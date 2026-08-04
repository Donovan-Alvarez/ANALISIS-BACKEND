package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.ModuloDTO;
import com.analisis_sistemas.erp.service.ModuloService;
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
@RequestMapping("/api/modulos")
public class ModuloController {

    private final ModuloService moduloService;

    public ModuloController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @GetMapping
    public ResponseEntity<List<ModuloDTO>> findAll() {
        return ResponseEntity.ok(moduloService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(moduloService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ModuloDTO> create(@Valid @RequestBody ModuloDTO dto) {
        ModuloDTO creado = moduloService.create(dto);
        return ResponseEntity.created(URI.create("/api/modulos/" + creado.getIdModulo())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuloDTO> update(@PathVariable Integer id, @Valid @RequestBody ModuloDTO dto) {
        return ResponseEntity.ok(moduloService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        moduloService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
