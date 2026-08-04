package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.MenuDTO;
import com.analisis_sistemas.erp.service.MenuService;
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
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuDTO>> findAll() {
        return ResponseEntity.ok(menuService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(menuService.findById(id));
    }

    @GetMapping("/por-modulo/{idModulo}")
    public ResponseEntity<List<MenuDTO>> findByModuloId(@PathVariable Integer idModulo) {
        return ResponseEntity.ok(menuService.findByModuloId(idModulo));
    }

    @PostMapping
    public ResponseEntity<MenuDTO> create(@Valid @RequestBody MenuDTO dto) {
        MenuDTO creado = menuService.create(dto);
        return ResponseEntity.created(URI.create("/api/menus/" + creado.getIdMenu())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuDTO> update(@PathVariable Integer id, @Valid @RequestBody MenuDTO dto) {
        return ResponseEntity.ok(menuService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
