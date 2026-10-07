package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.DepartamentoDTO;
import com.analisis_sistemas.erp.service.DepartamentoService;
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
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoDTO>> findAll() {
        return ResponseEntity.ok(departamentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(departamentoService.findById(id));
    }

    @GetMapping("/por-empresa/{idEmpresa}")
    public ResponseEntity<List<DepartamentoDTO>> findByEmpresaId(@PathVariable Integer idEmpresa) {
        return ResponseEntity.ok(departamentoService.findByEmpresaId(idEmpresa));
    }

    @PostMapping
    public ResponseEntity<DepartamentoDTO> create(@Valid @RequestBody DepartamentoDTO dto) {
        DepartamentoDTO creado = departamentoService.create(dto);
        return ResponseEntity.created(URI.create("/api/departamentos/" + creado.getIdDepartamento())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> update(@PathVariable Integer id, @Valid @RequestBody DepartamentoDTO dto) {
        return ResponseEntity.ok(departamentoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        departamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
