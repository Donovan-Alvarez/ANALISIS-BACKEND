package com.analisis_sistemas.erp.controller;
import com.analisis_sistemas.erp.dto.FlujoStatusEmpleadoDTO;
import com.analisis_sistemas.erp.service.FlujoStatusEmpleadoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/api/flujo-status-empleado")
public class FlujoStatusEmpleadoController {
 private final FlujoStatusEmpleadoService service;
 public FlujoStatusEmpleadoController(FlujoStatusEmpleadoService s){service=s;}
 @GetMapping public List<FlujoStatusEmpleadoDTO> listar(){return service.findAll();}
 @GetMapping("/{actual}/{nuevo}") public FlujoStatusEmpleadoDTO obtener(@PathVariable int actual,@PathVariable int nuevo){return service.findById(actual,nuevo);}
 @PostMapping public ResponseEntity<FlujoStatusEmpleadoDTO> crear(@Valid @RequestBody FlujoStatusEmpleadoDTO d){var r=service.create(d);return ResponseEntity.created(URI.create("/api/flujo-status-empleado/"+r.getIdStatusActual()+"/"+r.getIdStatusNuevo())).body(r);}
 @PutMapping("/{actual}/{nuevo}") public FlujoStatusEmpleadoDTO editar(@PathVariable int actual,@PathVariable int nuevo,@Valid @RequestBody FlujoStatusEmpleadoDTO d){return service.update(actual,nuevo,d);}
 @DeleteMapping("/{actual}/{nuevo}") public ResponseEntity<Void> eliminar(@PathVariable int actual,@PathVariable int nuevo){service.delete(actual,nuevo);return ResponseEntity.noContent().build();}
}
