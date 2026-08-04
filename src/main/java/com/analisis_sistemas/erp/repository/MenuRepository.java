package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Menu;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class MenuRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MenuRowMapper menuRowMapper;

    public MenuRepository(JdbcTemplate jdbcTemplate, MenuRowMapper menuRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.menuRowMapper = menuRowMapper;
    }

    public List<Menu> findAll() {
        String sql = """
                SELECT IdMenu, IdModulo, Nombre, OrdenMenu,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM MENU
                ORDER BY IdMenu
                """;
        return jdbcTemplate.query(sql, menuRowMapper);
    }

    public Optional<Menu> findById(Integer id) {
        String sql = """
                SELECT IdMenu, IdModulo, Nombre, OrdenMenu,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM MENU
                WHERE IdMenu = ?
                """;
        return jdbcTemplate.query(sql, menuRowMapper, id).stream().findFirst();
    }

    public List<Menu> findByModuloId(Integer idModulo) {
        String sql = """
                SELECT IdMenu, IdModulo, Nombre, OrdenMenu,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM MENU
                WHERE IdModulo = ?
                ORDER BY OrdenMenu
                """;
        return jdbcTemplate.query(sql, menuRowMapper, idModulo);
    }

    public Menu save(Menu menu) {
        String sql = """
                INSERT INTO MENU (
                    IdModulo, Nombre, OrdenMenu, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDMENU"});
            ps.setInt(1, menu.getIdModulo());
            ps.setString(2, menu.getNombre());
            ps.setInt(3, menu.getOrdenMenu());
            ps.setTimestamp(4, Timestamp.valueOf(menu.getFechaCreacion()));
            ps.setString(5, menu.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        menu.setIdMenu(keyHolder.getKey().intValue());
        return menu;
    }

    public int update(Menu menu) {
        String sql = """
                UPDATE MENU
                SET IdModulo = ?, Nombre = ?, OrdenMenu = ?,
                    FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdMenu = ?
                """;

        return jdbcTemplate.update(sql,
                menu.getIdModulo(),
                menu.getNombre(),
                menu.getOrdenMenu(),
                Timestamp.valueOf(menu.getFechaModificacion()),
                menu.getUsuarioModificacion(),
                menu.getIdMenu());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM MENU WHERE IdMenu = ?";
        return jdbcTemplate.update(sql, id);
    }
}
