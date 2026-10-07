package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.BancoDTO;
import com.analisis_sistemas.erp.service.BancoService;
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
@RequestMapping("/api/bancos")
public class BancoController {

    private final BancoService bancoService;

    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @GetMapping
    public ResponseEntity<List<BancoDTO>> findAll() {
        return ResponseEntity.ok(bancoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BancoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(bancoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BancoDTO> create(@Valid @RequestBody BancoDTO dto) {
        BancoDTO creado = bancoService.create(dto);
        return ResponseEntity.created(URI.create("/api/bancos/" + creado.getIdBanco())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BancoDTO> update(@PathVariable Integer id, @Valid @RequestBody BancoDTO dto) {
        return ResponseEntity.ok(bancoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bancoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
