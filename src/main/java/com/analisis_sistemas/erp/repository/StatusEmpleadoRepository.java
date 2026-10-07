package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.StatusEmpleado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class StatusEmpleadoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final StatusEmpleadoRowMapper statusEmpleadoRowMapper;

    public StatusEmpleadoRepository(JdbcTemplate jdbcTemplate, StatusEmpleadoRowMapper statusEmpleadoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.statusEmpleadoRowMapper = statusEmpleadoRowMapper;
    }

    public List<StatusEmpleado> findAll() {
        String sql = """
                SELECT IdStatusEmpleado, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM STATUS_EMPLEADO
                ORDER BY Nombre
                """;
        return jdbcTemplate.query(sql, statusEmpleadoRowMapper);
    }

    public Optional<StatusEmpleado> findById(Integer id) {
        String sql = """
                SELECT IdStatusEmpleado, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM STATUS_EMPLEADO
                WHERE IdStatusEmpleado = ?
                """;
        return jdbcTemplate.query(sql, statusEmpleadoRowMapper, id).stream().findFirst();
    }

    /** Duplicado sin distinguir mayusculas ni espacios de los extremos (alta). */
    public boolean existsByNombre(String nombre) {
        String sql = """
                SELECT COUNT(*)
                FROM STATUS_EMPLEADO
                WHERE UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, nombre);
        return total != null && total > 0;
    }

    /** Igual que {@link #existsByNombre(String)}, pero sin contar el propio registro (cambio). */
    public boolean existsByNombreExcluyendoId(String nombre, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM STATUS_EMPLEADO
                WHERE UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                  AND IdStatusEmpleado <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, nombre, idExcluir);
        return total != null && total > 0;
    }

    public StatusEmpleado save(StatusEmpleado statusEmpleado) {
        String sql = """
                INSERT INTO STATUS_EMPLEADO (
                    Nombre, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDSTATUSEMPLEADO"});
            ps.setString(1, statusEmpleado.getNombre());
            ps.setTimestamp(2, Timestamp.valueOf(statusEmpleado.getFechaCreacion()));
            ps.setString(3, statusEmpleado.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        statusEmpleado.setIdStatusEmpleado(keyHolder.getKey().intValue());
        return statusEmpleado;
    }

    public int update(StatusEmpleado statusEmpleado) {
        String sql = """
                UPDATE STATUS_EMPLEADO
                SET Nombre = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdStatusEmpleado = ?
                """;

        return jdbcTemplate.update(sql,
                statusEmpleado.getNombre(),
                Timestamp.valueOf(statusEmpleado.getFechaModificacion()),
                statusEmpleado.getUsuarioModificacion(),
                statusEmpleado.getIdStatusEmpleado());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM STATUS_EMPLEADO WHERE IdStatusEmpleado = ?";
        return jdbcTemplate.update(sql, id);
    }
}
