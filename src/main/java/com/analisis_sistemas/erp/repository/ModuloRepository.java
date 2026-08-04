package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Modulo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class ModuloRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ModuloRowMapper moduloRowMapper;

    public ModuloRepository(JdbcTemplate jdbcTemplate, ModuloRowMapper moduloRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.moduloRowMapper = moduloRowMapper;
    }

    public List<Modulo> findAll() {
        String sql = """
                SELECT IdModulo, Nombre, OrdenMenu,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM MODULO
                ORDER BY OrdenMenu
                """;
        return jdbcTemplate.query(sql, moduloRowMapper);
    }

    public Optional<Modulo> findById(Integer id) {
        String sql = """
                SELECT IdModulo, Nombre, OrdenMenu,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM MODULO
                WHERE IdModulo = ?
                """;
        return jdbcTemplate.query(sql, moduloRowMapper, id).stream().findFirst();
    }

    public Modulo save(Modulo modulo) {
        String sql = """
                INSERT INTO MODULO (
                    Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDMODULO"});
            ps.setString(1, modulo.getNombre());
            ps.setInt(2, modulo.getOrdenMenu());
            ps.setTimestamp(3, Timestamp.valueOf(modulo.getFechaCreacion()));
            ps.setString(4, modulo.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        modulo.setIdModulo(keyHolder.getKey().intValue());
        return modulo;
    }

    public int update(Modulo modulo) {
        String sql = """
                UPDATE MODULO
                SET Nombre = ?, OrdenMenu = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdModulo = ?
                """;

        return jdbcTemplate.update(sql,
                modulo.getNombre(),
                modulo.getOrdenMenu(),
                Timestamp.valueOf(modulo.getFechaModificacion()),
                modulo.getUsuarioModificacion(),
                modulo.getIdModulo());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM MODULO WHERE IdModulo = ?";
        return jdbcTemplate.update(sql, id);
    }
}
