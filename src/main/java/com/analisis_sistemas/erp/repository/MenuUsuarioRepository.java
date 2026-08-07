package com.analisis_sistemas.erp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuUsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public MenuUsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record FilaMenuRole(
            Integer idModulo, String moduloNombre, Integer moduloOrden,
            Integer idMenu, String menuNombre, Integer menuOrden,
            Integer idOpcion, String opcionNombre, Integer opcionOrden, String pagina,
            boolean alta, boolean baja, boolean cambio, boolean imprimir, boolean exportar
    ) {
    }

    public List<FilaMenuRole> findMenuPorRole(Integer idRole) {
        String sql = """
                SELECT mo.IdModulo, mo.Nombre AS ModuloNombre, mo.OrdenMenu AS ModuloOrden,
                       me.IdMenu, me.Nombre AS MenuNombre, me.OrdenMenu AS MenuOrden,
                       op.IdOpcion, op.Nombre AS OpcionNombre, op.OrdenMenu AS OpcionOrden, op.Pagina,
                       ro.Alta, ro.Baja, ro.Cambio, ro.Imprimir, ro.Exportar
                FROM ROLE_OPCION ro
                JOIN OPCION op ON op.IdOpcion = ro.IdOpcion
                JOIN MENU me ON me.IdMenu = op.IdMenu
                JOIN MODULO mo ON mo.IdModulo = me.IdModulo
                WHERE ro.IdRole = ?
                ORDER BY mo.OrdenMenu, me.OrdenMenu, op.OrdenMenu
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FilaMenuRole(
                rs.getInt("IdModulo"), rs.getString("ModuloNombre"), rs.getInt("ModuloOrden"),
                rs.getInt("IdMenu"), rs.getString("MenuNombre"), rs.getInt("MenuOrden"),
                rs.getInt("IdOpcion"), rs.getString("OpcionNombre"), rs.getInt("OpcionOrden"), rs.getString("Pagina"),
                rs.getInt("Alta") == 1, rs.getInt("Baja") == 1, rs.getInt("Cambio") == 1,
                rs.getInt("Imprimir") == 1, rs.getInt("Exportar") == 1
        ), idRole);
    }
}