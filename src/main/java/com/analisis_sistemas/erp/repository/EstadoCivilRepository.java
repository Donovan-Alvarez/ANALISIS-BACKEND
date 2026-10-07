package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.EstadoCivil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class EstadoCivilRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EstadoCivilRowMapper estadoCivilRowMapper;

    public EstadoCivilRepository(JdbcTemplate jdbcTemplate, EstadoCivilRowMapper estadoCivilRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.estadoCivilRowMapper = estadoCivilRowMapper;
    }

    public List<EstadoCivil> findAll() {
        String sql = """
                SELECT IdEstadoCivil, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM ESTADO_CIVIL
                ORDER BY Nombre
                """;
        return jdbcTemplate.query(sql, estadoCivilRowMapper);
    }

    public Optional<EstadoCivil> findById(Integer id) {
        String sql = """
                SELECT IdEstadoCivil, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM ESTADO_CIVIL
                WHERE IdEstadoCivil = ?
                """;
        return jdbcTemplate.query(sql, estadoCivilRowMapper, id).stream().findFirst();
    }

    /** Duplicado sin distinguir mayusculas ni espacios de los extremos (alta). */
    public boolean existsByNombre(String nombre) {
        String sql = """
                SELECT COUNT(*)
                FROM ESTADO_CIVIL
                WHERE UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, nombre);
        return total != null && total > 0;
    }

    /** Igual que {@link #existsByNombre(String)}, pero sin contar el propio registro (cambio). */
    public boolean existsByNombreExcluyendoId(String nombre, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM ESTADO_CIVIL
                WHERE UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                  AND IdEstadoCivil <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, nombre, idExcluir);
        return total != null && total > 0;
    }

    public EstadoCivil save(EstadoCivil estadoCivil) {
        String sql = """
                INSERT INTO ESTADO_CIVIL (
                    Nombre, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDESTADOCIVIL"});
            ps.setString(1, estadoCivil.getNombre());
            ps.setTimestamp(2, Timestamp.valueOf(estadoCivil.getFechaCreacion()));
            ps.setString(3, estadoCivil.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        estadoCivil.setIdEstadoCivil(keyHolder.getKey().intValue());
        return estadoCivil;
    }

    public int update(EstadoCivil estadoCivil) {
        String sql = """
                UPDATE ESTADO_CIVIL
                SET Nombre = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdEstadoCivil = ?
                """;

        return jdbcTemplate.update(sql,
                estadoCivil.getNombre(),
                Timestamp.valueOf(estadoCivil.getFechaModificacion()),
                estadoCivil.getUsuarioModificacion(),
                estadoCivil.getIdEstadoCivil());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM ESTADO_CIVIL WHERE IdEstadoCivil = ?";
        return jdbcTemplate.update(sql, id);
    }
}
