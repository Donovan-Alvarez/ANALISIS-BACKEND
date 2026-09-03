package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RoleRowMapper roleRowMapper;

    public RoleRepository(JdbcTemplate jdbcTemplate, RoleRowMapper roleRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleRowMapper = roleRowMapper;
    }

    public List<Role> findAll() {
        String sql = """
                SELECT IdRole, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM "ROLE"
                ORDER BY IdRole
                """;
        return jdbcTemplate.query(sql, roleRowMapper);
    }

    public Optional<Role> findById(Integer id) {
        String sql = """
                SELECT IdRole, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM "ROLE"
                WHERE IdRole = ?
                """;
        return jdbcTemplate.query(sql, roleRowMapper, id).stream().findFirst();
    }

    public Role save(Role role) {
        String sql = """
                INSERT INTO "ROLE" (
                    Nombre, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDROLE"});
            ps.setString(1, role.getNombre());
            ps.setTimestamp(2, Timestamp.valueOf(role.getFechaCreacion()));
            ps.setString(3, role.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        role.setIdRole(keyHolder.getKey().intValue());
        return role;
    }

    public int update(Role role) {
        String sql = """
                UPDATE "ROLE"
                SET Nombre = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdRole = ?
                """;

        return jdbcTemplate.update(sql,
                role.getNombre(),
                Timestamp.valueOf(role.getFechaModificacion()),
                role.getUsuarioModificacion(),
                role.getIdRole());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM \"ROLE\" WHERE IdRole = ?";
        return jdbcTemplate.update(sql, id);
    }
}
