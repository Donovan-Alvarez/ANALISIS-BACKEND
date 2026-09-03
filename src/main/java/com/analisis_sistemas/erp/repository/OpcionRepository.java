package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Opcion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class OpcionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final OpcionRowMapper opcionRowMapper;

    public OpcionRepository(JdbcTemplate jdbcTemplate, OpcionRowMapper opcionRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.opcionRowMapper = opcionRowMapper;
    }

    public List<Opcion> findAll() {
        String sql = """
                SELECT IdOpcion, IdMenu, Nombre, OrdenMenu, Pagina,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM OPCION
                ORDER BY IdOpcion
                """;
        return jdbcTemplate.query(sql, opcionRowMapper);
    }

    public Optional<Opcion> findById(Integer id) {
        String sql = """
                SELECT IdOpcion, IdMenu, Nombre, OrdenMenu, Pagina,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM OPCION
                WHERE IdOpcion = ?
                """;
        return jdbcTemplate.query(sql, opcionRowMapper, id).stream().findFirst();
    }

    public Optional<Integer> findIdOpcionPorPagina(String pagina) {
        String sql = "SELECT IdOpcion FROM OPCION WHERE Pagina = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("IdOpcion"), pagina).stream().findFirst();
    }

    public List<Opcion> findByMenuId(Integer idMenu) {
        String sql = """
                SELECT IdOpcion, IdMenu, Nombre, OrdenMenu, Pagina,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM OPCION
                WHERE IdMenu = ?
                ORDER BY OrdenMenu
                """;
        return jdbcTemplate.query(sql, opcionRowMapper, idMenu);
    }

    public Opcion save(Opcion opcion) {
        String sql = """
                INSERT INTO OPCION (
                    IdMenu, Nombre, OrdenMenu, Pagina, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDOPCION"});
            ps.setInt(1, opcion.getIdMenu());
            ps.setString(2, opcion.getNombre());
            ps.setInt(3, opcion.getOrdenMenu());
            ps.setString(4, opcion.getPagina());
            ps.setTimestamp(5, Timestamp.valueOf(opcion.getFechaCreacion()));
            ps.setString(6, opcion.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        opcion.setIdOpcion(keyHolder.getKey().intValue());
        return opcion;
    }

    public int update(Opcion opcion) {
        String sql = """
                UPDATE OPCION
                SET IdMenu = ?, Nombre = ?, OrdenMenu = ?, Pagina = ?,
                    FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdOpcion = ?
                """;

        return jdbcTemplate.update(sql,
                opcion.getIdMenu(),
                opcion.getNombre(),
                opcion.getOrdenMenu(),
                opcion.getPagina(),
                Timestamp.valueOf(opcion.getFechaModificacion()),
                opcion.getUsuarioModificacion(),
                opcion.getIdOpcion());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM OPCION WHERE IdOpcion = ?";
        return jdbcTemplate.update(sql, id);
    }
}
