package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.OpcionDTO;
import com.analisis_sistemas.erp.service.OpcionService;
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
@RequestMapping("/api/opciones")
public class OpcionController {

    private final OpcionService opcionService;

    public OpcionController(OpcionService opcionService) {
        this.opcionService = opcionService;
    }

    @GetMapping
    public ResponseEntity<List<OpcionDTO>> findAll() {
        return ResponseEntity.ok(opcionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpcionDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(opcionService.findById(id));
    }

    @GetMapping("/por-menu/{idMenu}")
    public ResponseEntity<List<OpcionDTO>> findByMenuId(@PathVariable Integer idMenu) {
        return ResponseEntity.ok(opcionService.findByMenuId(idMenu));
    }

    @PostMapping
    public ResponseEntity<OpcionDTO> create(@Valid @RequestBody OpcionDTO dto) {
        OpcionDTO creada = opcionService.create(dto);
        return ResponseEntity.created(URI.create("/api/opciones/" + creada.getIdOpcion())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpcionDTO> update(@PathVariable Integer id, @Valid @RequestBody OpcionDTO dto) {
        return ResponseEntity.ok(opcionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        opcionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
