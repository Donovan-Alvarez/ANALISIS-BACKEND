package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.TipoDocumentoDTO;
import com.analisis_sistemas.erp.service.TipoDocumentoService;
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
@RequestMapping("/api/tipos-documento")
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    public TipoDocumentoController(TipoDocumentoService tipoDocumentoService) {
        this.tipoDocumentoService = tipoDocumentoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoDocumentoDTO>> findAll() {
        return ResponseEntity.ok(tipoDocumentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumentoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(tipoDocumentoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TipoDocumentoDTO> create(@Valid @RequestBody TipoDocumentoDTO dto) {
        TipoDocumentoDTO creado = tipoDocumentoService.create(dto);
        return ResponseEntity.created(URI.create("/api/tipos-documento/" + creado.getIdTipoDocumento())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumentoDTO> update(@PathVariable Integer id, @Valid @RequestBody TipoDocumentoDTO dto) {
        return ResponseEntity.ok(tipoDocumentoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tipoDocumentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
