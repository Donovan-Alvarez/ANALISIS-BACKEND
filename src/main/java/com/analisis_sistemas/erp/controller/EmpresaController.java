package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.EmpresaDTO;
import com.analisis_sistemas.erp.service.EmpresaService;
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
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<List<EmpresaDTO>> findAll() {
        return ResponseEntity.ok(empresaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(empresaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaDTO> create(@Valid @RequestBody EmpresaDTO dto) {
        EmpresaDTO creada = empresaService.create(dto);
        return ResponseEntity.created(URI.create("/api/empresas/" + creada.getIdEmpresa())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> update(@PathVariable Integer id, @Valid @RequestBody EmpresaDTO dto) {
        return ResponseEntity.ok(empresaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        empresaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
