package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.PuestoDTO;
import com.analisis_sistemas.erp.service.PuestoService;
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
@RequestMapping("/api/puestos")
public class PuestoController {

    private final PuestoService puestoService;

    public PuestoController(PuestoService puestoService) {
        this.puestoService = puestoService;
    }

    @GetMapping
    public ResponseEntity<List<PuestoDTO>> findAll() {
        return ResponseEntity.ok(puestoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuestoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(puestoService.findById(id));
    }

    @GetMapping("/por-departamento/{idDepartamento}")
    public ResponseEntity<List<PuestoDTO>> findByDepartamentoId(@PathVariable Integer idDepartamento) {
        return ResponseEntity.ok(puestoService.findByDepartamentoId(idDepartamento));
    }

    @GetMapping("/por-empresa/{idEmpresa}")
    public ResponseEntity<List<PuestoDTO>> findByEmpresaId(@PathVariable Integer idEmpresa) {
        return ResponseEntity.ok(puestoService.findByEmpresaId(idEmpresa));
    }

    @PostMapping
    public ResponseEntity<PuestoDTO> create(@Valid @RequestBody PuestoDTO dto) {
        PuestoDTO creado = puestoService.create(dto);
        return ResponseEntity.created(URI.create("/api/puestos/" + creado.getIdPuesto())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuestoDTO> update(@PathVariable Integer id, @Valid @RequestBody PuestoDTO dto) {
        return ResponseEntity.ok(puestoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        puestoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
