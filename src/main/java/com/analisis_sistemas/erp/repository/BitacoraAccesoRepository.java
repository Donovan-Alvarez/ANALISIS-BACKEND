package com.analisis_sistemas.erp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BitacoraAccesoRepository {

    private final JdbcTemplate jdbcTemplate;

    public BitacoraAccesoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertar(String idUsuario, Integer idTipoAcceso, String httpUserAgent, String direccionIp, String acceso, String sesion) {
        String sql = """
                INSERT INTO BITACORA_ACCESO (
                    IdUsuario, IdTipoAcceso, FechaAcceso, HttpUserAgent, DireccionIp, Acceso, Sesion
                ) VALUES (?, ?, SYSTIMESTAMP, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, idUsuario, idTipoAcceso, httpUserAgent, direccionIp, acceso, sesion);
    }
}
