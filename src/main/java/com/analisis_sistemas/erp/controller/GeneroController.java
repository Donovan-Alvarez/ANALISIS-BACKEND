package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.GeneroDTO;
import com.analisis_sistemas.erp.service.GeneroService;
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
@RequestMapping("/api/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    @GetMapping
    public ResponseEntity<List<GeneroDTO>> findAll() {
        return ResponseEntity.ok(generoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneroDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(generoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<GeneroDTO> create(@Valid @RequestBody GeneroDTO dto) {
        GeneroDTO creado = generoService.create(dto);
        return ResponseEntity.created(URI.create("/api/generos/" + creado.getIdGenero())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneroDTO> update(@PathVariable Integer id, @Valid @RequestBody GeneroDTO dto) {
        return ResponseEntity.ok(generoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        generoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
