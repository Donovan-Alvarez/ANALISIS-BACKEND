package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.EstadoCivilDTO;
import com.analisis_sistemas.erp.service.EstadoCivilService;
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
@RequestMapping("/api/estados-civiles")
public class EstadoCivilController {

    private final EstadoCivilService estadoCivilService;

    public EstadoCivilController(EstadoCivilService estadoCivilService) {
        this.estadoCivilService = estadoCivilService;
    }

    @GetMapping
    public ResponseEntity<List<EstadoCivilDTO>> findAll() {
        return ResponseEntity.ok(estadoCivilService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoCivilDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(estadoCivilService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EstadoCivilDTO> create(@Valid @RequestBody EstadoCivilDTO dto) {
        EstadoCivilDTO creado = estadoCivilService.create(dto);
        return ResponseEntity.created(URI.create("/api/estados-civiles/" + creado.getIdEstadoCivil())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoCivilDTO> update(@PathVariable Integer id, @Valid @RequestBody EstadoCivilDTO dto) {
        return ResponseEntity.ok(estadoCivilService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        estadoCivilService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
