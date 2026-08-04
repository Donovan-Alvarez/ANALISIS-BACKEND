package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Genero;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class GeneroRepository {

    private final JdbcTemplate jdbcTemplate;
    private final GeneroRowMapper generoRowMapper;

    public GeneroRepository(JdbcTemplate jdbcTemplate, GeneroRowMapper generoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.generoRowMapper = generoRowMapper;
    }

    public List<Genero> findAll() {
        String sql = """
                SELECT IdGenero, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM GENERO
                ORDER BY IdGenero
                """;
        return jdbcTemplate.query(sql, generoRowMapper);
    }

    public Optional<Genero> findById(Integer id) {
        String sql = """
                SELECT IdGenero, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM GENERO
                WHERE IdGenero = ?
                """;
        return jdbcTemplate.query(sql, generoRowMapper, id).stream().findFirst();
    }

    public Genero save(Genero genero) {
        String sql = """
                INSERT INTO GENERO (
                    Nombre, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDGENERO"});
            ps.setString(1, genero.getNombre());
            ps.setTimestamp(2, Timestamp.valueOf(genero.getFechaCreacion()));
            ps.setString(3, genero.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        genero.setIdGenero(keyHolder.getKey().intValue());
        return genero;
    }

    public int update(Genero genero) {
        String sql = """
                UPDATE GENERO
                SET Nombre = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdGenero = ?
                """;

        return jdbcTemplate.update(sql,
                genero.getNombre(),
                Timestamp.valueOf(genero.getFechaModificacion()),
                genero.getUsuarioModificacion(),
                genero.getIdGenero());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM GENERO WHERE IdGenero = ?";
        return jdbcTemplate.update(sql, id);
    }
}
