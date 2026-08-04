package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.TipoAcceso;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TipoAccesoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TipoAccesoRowMapper tipoAccesoRowMapper;

    public TipoAccesoRepository(JdbcTemplate jdbcTemplate, TipoAccesoRowMapper tipoAccesoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tipoAccesoRowMapper = tipoAccesoRowMapper;
    }

    public Optional<TipoAcceso> findByNombre(String nombre) {
        String sql = """
                SELECT IdTipoAcceso, Nombre
                FROM TIPO_ACCESO
                WHERE Nombre = ?
                """;
        return jdbcTemplate.query(sql, tipoAccesoRowMapper, nombre).stream().findFirst();
    }
}
