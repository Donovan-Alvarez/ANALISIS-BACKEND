package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.PersonaDTO;
import com.analisis_sistemas.erp.service.PersonaService;
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

/** El primer segmento (/api/personas) debe ser igual a OPCION.Pagina = 'personas' (contrato-rutas.md). */
@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> findAll() {
        return ResponseEntity.ok(personaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(personaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PersonaDTO> create(@Valid @RequestBody PersonaDTO dto) {
        PersonaDTO creada = personaService.create(dto);
        return ResponseEntity.created(URI.create("/api/personas/" + creada.getIdPersona())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaDTO> update(@PathVariable Integer id, @Valid @RequestBody PersonaDTO dto) {
        return ResponseEntity.ok(personaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        personaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
