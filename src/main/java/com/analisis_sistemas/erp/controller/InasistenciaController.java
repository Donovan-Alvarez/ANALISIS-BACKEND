package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.EmpleadoOpcionDTO;
import com.analisis_sistemas.erp.dto.InasistenciaDTO;
import com.analisis_sistemas.erp.service.InasistenciaService;
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

/** El primer segmento de la URL ('inasistencias') es OPCION.Pagina: asi PermisoAccionInterceptor resuelve el permiso. */
@RestController
@RequestMapping("/api/inasistencias")
public class InasistenciaController {

    private final InasistenciaService inasistenciaService;

    public InasistenciaController(InasistenciaService inasistenciaService) {
        this.inasistenciaService = inasistenciaService;
    }

    @GetMapping
    public ResponseEntity<List<InasistenciaDTO>> findAll() {
        return ResponseEntity.ok(inasistenciaService.findAll());
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<EmpleadoOpcionDTO>> findEmpleadosActivos() {
        return ResponseEntity.ok(inasistenciaService.findEmpleadosActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InasistenciaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(inasistenciaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<InasistenciaDTO> create(@Valid @RequestBody InasistenciaDTO dto) {
        InasistenciaDTO creada = inasistenciaService.create(dto);
        return ResponseEntity.created(URI.create("/api/inasistencias/" + creada.getIdInasistencia())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InasistenciaDTO> update(@PathVariable Integer id, @Valid @RequestBody InasistenciaDTO dto) {
        return ResponseEntity.ok(inasistenciaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        inasistenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}