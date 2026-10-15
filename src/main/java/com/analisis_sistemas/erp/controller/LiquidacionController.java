package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.EmpleadoLiquidableDTO;
import com.analisis_sistemas.erp.dto.LiquidacionDTO;
import com.analisis_sistemas.erp.dto.LiquidacionRequestDTO;
import com.analisis_sistemas.erp.service.LiquidacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * La ruta base tiene que ser "liquidacion" (singular): es el valor de
 * OPCION.Pagina que sembro 04_fase2_rbac.sql, y PermisoAccionInterceptor
 * lo usa para resolver los permisos. Con cualquier otro nombre, el POST
 * responderia 500.
 * <p>
 * No hay PUT ni DELETE a proposito: una liquidacion emitida es historial
 * permanente. Los permisos Cambio y Baja de esta opcion quedan sin uso.
 */
@RestController
@RequestMapping("/api/liquidacion")
public class LiquidacionController {

    private final LiquidacionService liquidacionService;

    public LiquidacionController(LiquidacionService liquidacionService) {
        this.liquidacionService = liquidacionService;
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionDTO>> findAll() {
        return ResponseEntity.ok(liquidacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(liquidacionService.findById(id));
    }

    /** Empleados que todavía pueden liquidarse, para el combo de la pantalla. */
    @GetMapping("/empleados-liquidables")
    public ResponseEntity<List<EmpleadoLiquidableDTO>> findEmpleadosLiquidables() {
        return ResponseEntity.ok(liquidacionService.findEmpleadosLiquidables());
    }

    @PostMapping
    public ResponseEntity<LiquidacionDTO> calcular(@Valid @RequestBody LiquidacionRequestDTO dto) {
        LiquidacionDTO creada = liquidacionService.calcular(dto);
        return ResponseEntity.created(URI.create("/api/liquidacion/" + creada.getIdLiquidacion()))
                .body(creada);
    }
}
