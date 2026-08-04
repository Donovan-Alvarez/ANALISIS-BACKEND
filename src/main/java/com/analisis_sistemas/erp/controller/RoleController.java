package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.RoleDTO;
import com.analisis_sistemas.erp.service.RoleService;
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
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RoleDTO> create(@Valid @RequestBody RoleDTO dto) {
        RoleDTO creado = roleService.create(dto);
        return ResponseEntity.created(URI.create("/api/roles/" + creado.getIdRole())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Integer id, @Valid @RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
