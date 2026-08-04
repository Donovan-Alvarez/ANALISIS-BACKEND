package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.SucursalDTO;
import com.analisis_sistemas.erp.service.SucursalService;
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
@RequestMapping("/api/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public ResponseEntity<List<SucursalDTO>> findAll() {
        return ResponseEntity.ok(sucursalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(sucursalService.findById(id));
    }

    @GetMapping("/por-empresa/{idEmpresa}")
    public ResponseEntity<List<SucursalDTO>> findByEmpresaId(@PathVariable Integer idEmpresa) {
        return ResponseEntity.ok(sucursalService.findByEmpresaId(idEmpresa));
    }

    @PostMapping
    public ResponseEntity<SucursalDTO> create(@Valid @RequestBody SucursalDTO dto) {
        SucursalDTO creada = sucursalService.create(dto);
        return ResponseEntity.created(URI.create("/api/sucursales/" + creada.getIdSucursal())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalDTO> update(@PathVariable Integer id, @Valid @RequestBody SucursalDTO dto) {
        return ResponseEntity.ok(sucursalService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sucursalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
