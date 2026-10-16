package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.CuentaBancariaEmpleadoDTO;
import com.analisis_sistemas.erp.dto.EmpleadoOpcionDTO;
import com.analisis_sistemas.erp.service.CuentaBancariaEmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** El primer segmento de la URL ('cuentas-bancarias') es OPCION.Pagina: asi PermisoAccionInterceptor resuelve el permiso. */
@RestController
@RequestMapping("/api/cuentas-bancarias")
public class CuentaBancariaEmpleadoController {

    private final CuentaBancariaEmpleadoService cuentaService;

    public CuentaBancariaEmpleadoController(CuentaBancariaEmpleadoService cuentaService) {
        this.cuentaService = cuentaService;
    }

    /** ?soloActivas=true y/o ?idEmpleado=N filtran la lista (lo que necesita Boletas de Pago). */
    @GetMapping
    public ResponseEntity<List<CuentaBancariaEmpleadoDTO>> findAll(
            @RequestParam(defaultValue = "false") boolean soloActivas,
            @RequestParam(required = false) Integer idEmpleado) {
        return ResponseEntity.ok(cuentaService.findAll(soloActivas, idEmpleado));
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<EmpleadoOpcionDTO>> findEmpleados() {
        return ResponseEntity.ok(cuentaService.findEmpleados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaBancariaEmpleadoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(cuentaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CuentaBancariaEmpleadoDTO> create(@Valid @RequestBody CuentaBancariaEmpleadoDTO dto) {
        CuentaBancariaEmpleadoDTO creada = cuentaService.create(dto);
        return ResponseEntity.created(URI.create("/api/cuentas-bancarias/" + creada.getIdCuentaBancaria())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaBancariaEmpleadoDTO> update(@PathVariable Integer id,
                                                            @Valid @RequestBody CuentaBancariaEmpleadoDTO dto) {
        return ResponseEntity.ok(cuentaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        cuentaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
