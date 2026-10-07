package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.StatusEmpleadoDTO;
import com.analisis_sistemas.erp.service.StatusEmpleadoService;
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
@RequestMapping("/api/status-empleado")
public class StatusEmpleadoController {

    private final StatusEmpleadoService statusEmpleadoService;

    public StatusEmpleadoController(StatusEmpleadoService statusEmpleadoService) {
        this.statusEmpleadoService = statusEmpleadoService;
    }

    @GetMapping
    public ResponseEntity<List<StatusEmpleadoDTO>> findAll() {
        return ResponseEntity.ok(statusEmpleadoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusEmpleadoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(statusEmpleadoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<StatusEmpleadoDTO> create(@Valid @RequestBody StatusEmpleadoDTO dto) {
        StatusEmpleadoDTO creado = statusEmpleadoService.create(dto);
        return ResponseEntity.created(URI.create("/api/status-empleado/" + creado.getIdStatusEmpleado())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusEmpleadoDTO> update(@PathVariable Integer id, @Valid @RequestBody StatusEmpleadoDTO dto) {
        return ResponseEntity.ok(statusEmpleadoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        statusEmpleadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
