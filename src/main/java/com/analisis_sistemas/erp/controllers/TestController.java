package com.analisis_sistemas.erp.controllers;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class TestController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/test/db")
    public List<Map<String, Object>> testConexion() {
        return jdbcTemplate.queryForList("SELECT IdUsuario, Nombre, Apellido FROM USUARIO");
    }


}
