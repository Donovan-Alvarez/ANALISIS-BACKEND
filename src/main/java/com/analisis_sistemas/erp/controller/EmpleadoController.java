package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.EmpleadoDTO;
import com.analisis_sistemas.erp.dto.PersonaComboDTO;
import com.analisis_sistemas.erp.dto.StatusPermitidoDTO;
import com.analisis_sistemas.erp.service.EmpleadoService;
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

/**
 * La ruta base NO es arbitraria: PermisoAccionInterceptor saca el slug del
 * primer segmento ("empleados") y busca OPCION.Pagina con ese valor para
 * decidir si el rol puede hacer POST/PUT/DELETE. Si se renombra la ruta sin
 * renombrar tambien OPCION.Pagina, toda escritura responde 500.
 */
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> findAll() {
        return ResponseEntity.ok(empleadoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(empleadoService.findById(id));
    }

    /** Status a los que este empleado puede moverse, para el combo de la pantalla. */
    @GetMapping("/{id}/status-permitidos")
    public ResponseEntity<List<StatusPermitidoDTO>> findStatusPermitidos(@PathVariable Integer id) {
        return ResponseEntity.ok(empleadoService.findStatusPermitidos(id));
    }

    /**
     * Personas para el combo. PROVISIONAL: vive aqui y no en /api/personas
     * para no colisionar con la pantalla de Personas cuando se construya.
     */
    @GetMapping("/personas")
    public ResponseEntity<List<PersonaComboDTO>> findPersonasCombo() {
        return ResponseEntity.ok(empleadoService.findPersonasCombo());
    }

    @PostMapping
    public ResponseEntity<EmpleadoDTO> create(@Valid @RequestBody EmpleadoDTO dto) {
        EmpleadoDTO creado = empleadoService.create(dto);
        return ResponseEntity.created(URI.create("/api/empleados/" + creado.getIdEmpleado())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> update(@PathVariable Integer id, @Valid @RequestBody EmpleadoDTO dto) {
        return ResponseEntity.ok(empleadoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        empleadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
