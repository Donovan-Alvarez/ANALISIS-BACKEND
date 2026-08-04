package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.StatusUsuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class StatusUsuarioRepository {

    private final JdbcTemplate jdbcTemplate;
    private final StatusUsuarioRowMapper statusUsuarioRowMapper;

    public StatusUsuarioRepository(JdbcTemplate jdbcTemplate, StatusUsuarioRowMapper statusUsuarioRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.statusUsuarioRowMapper = statusUsuarioRowMapper;
    }

    public List<StatusUsuario> findAll() {
        String sql = """
                SELECT IdStatusUsuario, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM STATUS_USUARIO
                ORDER BY IdStatusUsuario
                """;
        return jdbcTemplate.query(sql, statusUsuarioRowMapper);
    }

    public Optional<StatusUsuario> findById(Integer id) {
        String sql = """
                SELECT IdStatusUsuario, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM STATUS_USUARIO
                WHERE IdStatusUsuario = ?
                """;
        return jdbcTemplate.query(sql, statusUsuarioRowMapper, id).stream().findFirst();
    }

    public StatusUsuario save(StatusUsuario statusUsuario) {
        String sql = """
                INSERT INTO STATUS_USUARIO (
                    Nombre, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDSTATUSUSUARIO"});
            ps.setString(1, statusUsuario.getNombre());
            ps.setTimestamp(2, Timestamp.valueOf(statusUsuario.getFechaCreacion()));
            ps.setString(3, statusUsuario.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        statusUsuario.setIdStatusUsuario(keyHolder.getKey().intValue());
        return statusUsuario;
    }

    public Optional<StatusUsuario> findByNombre(String nombre) {
        String sql = """
                SELECT IdStatusUsuario, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM STATUS_USUARIO
                WHERE Nombre = ?
                """;
        return jdbcTemplate.query(sql, statusUsuarioRowMapper, nombre).stream().findFirst();
    }

    public int update(StatusUsuario statusUsuario) {
        String sql = """
                UPDATE STATUS_USUARIO
                SET Nombre = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdStatusUsuario = ?
                """;

        return jdbcTemplate.update(sql,
                statusUsuario.getNombre(),
                Timestamp.valueOf(statusUsuario.getFechaModificacion()),
                statusUsuario.getUsuarioModificacion(),
                statusUsuario.getIdStatusUsuario());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM STATUS_USUARIO WHERE IdStatusUsuario = ?";
        return jdbcTemplate.update(sql, id);
    }
}
