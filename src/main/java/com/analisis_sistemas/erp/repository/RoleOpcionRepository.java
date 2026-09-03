package com.analisis_sistemas.erp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RoleOpcionRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoleOpcionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record FilaOpcionRole(
            Integer idMenu, String menuNombre, Integer menuOrden,
            Integer idOpcion, String opcionNombre, Integer opcionOrden,
            boolean consultar, boolean alta, boolean baja, boolean cambio, boolean imprimir, boolean exportar
    ) {
    }

    public List<FilaOpcionRole> findOpcionesPorRoleYModulo(Integer idRole, Integer idModulo) {
        String sql = """
                SELECT me.IdMenu, me.Nombre AS MenuNombre, me.OrdenMenu AS MenuOrden,
                       op.IdOpcion, op.Nombre AS OpcionNombre, op.OrdenMenu AS OpcionOrden,
                       CASE WHEN ro.IdRole IS NOT NULL THEN 1 ELSE 0 END AS Consultar,
                       NVL(ro.Alta, 0)     AS Alta,
                       NVL(ro.Baja, 0)     AS Baja,
                       NVL(ro.Cambio, 0)   AS Cambio,
                       NVL(ro.Imprimir, 0) AS Imprimir,
                       NVL(ro.Exportar, 0) AS Exportar
                FROM OPCION op
                JOIN MENU me ON me.IdMenu = op.IdMenu
                LEFT JOIN ROLE_OPCION ro ON ro.IdOpcion = op.IdOpcion AND ro.IdRole = ?
                WHERE me.IdModulo = ?
                ORDER BY me.OrdenMenu, op.OrdenMenu
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FilaOpcionRole(
                rs.getInt("IdMenu"), rs.getString("MenuNombre"), rs.getInt("MenuOrden"),
                rs.getInt("IdOpcion"), rs.getString("OpcionNombre"), rs.getInt("OpcionOrden"),
                rs.getInt("Consultar") == 1, rs.getInt("Alta") == 1, rs.getInt("Baja") == 1,
                rs.getInt("Cambio") == 1, rs.getInt("Imprimir") == 1, rs.getInt("Exportar") == 1
        ), idRole, idModulo);
    }

    public record PermisoFlags(boolean alta, boolean baja, boolean cambio) {
    }

    public Optional<PermisoFlags> findFlags(Integer idRole, Integer idOpcion) {
        String sql = """
                SELECT Alta, Baja, Cambio
                FROM ROLE_OPCION
                WHERE IdRole = ? AND IdOpcion = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new PermisoFlags(
                rs.getInt("Alta") == 1, rs.getInt("Baja") == 1, rs.getInt("Cambio") == 1
        ), idRole, idOpcion).stream().findFirst();
    }

    public List<Integer> findIdsOpcionesPorModulo(Integer idModulo) {
        String sql = """
                SELECT op.IdOpcion
                FROM OPCION op
                JOIN MENU me ON me.IdMenu = op.IdMenu
                WHERE me.IdModulo = ?
                """;
        return jdbcTemplate.queryForList(sql, Integer.class, idModulo);
    }

    public int deletePermisos(Integer idRole, List<Integer> idsOpciones) {
        if (idsOpciones.isEmpty()) {
            return 0;
        }

        String placeholders = idsOpciones.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = "DELETE FROM ROLE_OPCION WHERE IdRole = ? AND IdOpcion IN (" + placeholders + ")";

        List<Object> params = new ArrayList<>();
        params.add(idRole);
        params.addAll(idsOpciones);

        return jdbcTemplate.update(sql, params.toArray());
    }

    public void insertPermiso(Integer idRole, Integer idOpcion, boolean alta, boolean baja, boolean cambio,
                               boolean imprimir, boolean exportar, String usuarioCreacion, LocalDateTime fechaCreacion) {
        String sql = """
                INSERT INTO ROLE_OPCION (IdRole, IdOpcion, Alta, Baja, Cambio, Imprimir, Exportar, FechaCreacion, UsuarioCreacion)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, ps -> {
            ps.setInt(1, idRole);
            ps.setInt(2, idOpcion);
            ps.setInt(3, alta ? 1 : 0);
            ps.setInt(4, baja ? 1 : 0);
            ps.setInt(5, cambio ? 1 : 0);
            ps.setInt(6, imprimir ? 1 : 0);
            ps.setInt(7, exportar ? 1 : 0);
            ps.setTimestamp(8, Timestamp.valueOf(fechaCreacion));
            ps.setString(9, usuarioCreacion);
        });
    }
}
