package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Puesto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PuestoRepository {

    private static final String SELECT_BASE = """
            SELECT p.IdPuesto, p.Nombre, p.IdDepartamento, d.Nombre AS NombreDepartamento,
                   d.IdEmpresa, e.Nombre AS NombreEmpresa,
                   p.FechaCreacion, p.UsuarioCreacion, p.FechaModificacion, p.UsuarioModificacion
            FROM PUESTO p
            JOIN DEPARTAMENTO d ON d.IdDepartamento = p.IdDepartamento
            LEFT JOIN EMPRESA e ON e.IdEmpresa = d.IdEmpresa
            """;

    private final JdbcTemplate jdbcTemplate;
    private final PuestoRowMapper puestoRowMapper;

    public PuestoRepository(JdbcTemplate jdbcTemplate, PuestoRowMapper puestoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.puestoRowMapper = puestoRowMapper;
    }

    public List<Puesto> findAll() {
        String sql = SELECT_BASE + "ORDER BY e.Nombre, d.Nombre, p.Nombre";
        return jdbcTemplate.query(sql, puestoRowMapper);
    }

    public Optional<Puesto> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE p.IdPuesto = ?";
        return jdbcTemplate.query(sql, puestoRowMapper, id).stream().findFirst();
    }

    public List<Puesto> findByDepartamentoId(Integer idDepartamento) {
        String sql = SELECT_BASE + "WHERE p.IdDepartamento = ? ORDER BY p.Nombre";
        return jdbcTemplate.query(sql, puestoRowMapper, idDepartamento);
    }

    public List<Puesto> findByEmpresaId(Integer idEmpresa) {
        String sql = SELECT_BASE + "WHERE d.IdEmpresa = ? ORDER BY d.Nombre, p.Nombre";
        return jdbcTemplate.query(sql, puestoRowMapper, idEmpresa);
    }

    public boolean existsByDepartamentoYNombre(Integer idDepartamento, String nombre) {
        String sql = """
                SELECT COUNT(*)
                FROM PUESTO
                WHERE IdDepartamento = ?
                  AND UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idDepartamento, nombre);
        return total != null && total > 0;
    }

    public boolean existsByDepartamentoYNombreExcluyendoId(Integer idDepartamento, String nombre, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM PUESTO
                WHERE IdDepartamento = ?
                  AND UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                  AND IdPuesto <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idDepartamento, nombre, idExcluir);
        return total != null && total > 0;
    }

    public int countEmpleados(Integer idPuesto) {
        String sql = "SELECT COUNT(*) FROM EMPLEADO WHERE IdPuesto = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idPuesto);
        return total != null ? total : 0;
    }

    public Puesto save(Puesto puesto) {
        String sql = """
                INSERT INTO PUESTO (
                    Nombre, IdDepartamento, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDPUESTO"});
            ps.setString(1, puesto.getNombre());
            ps.setInt(2, puesto.getIdDepartamento());
            ps.setTimestamp(3, Timestamp.valueOf(puesto.getFechaCreacion()));
            ps.setString(4, puesto.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        puesto.setIdPuesto(keyHolder.getKey().intValue());
        return puesto;
    }

    public int update(Puesto puesto) {
        String sql = """
                UPDATE PUESTO
                SET Nombre = ?, IdDepartamento = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdPuesto = ?
                """;

        return jdbcTemplate.update(sql,
                puesto.getNombre(),
                puesto.getIdDepartamento(),
                Timestamp.valueOf(puesto.getFechaModificacion()),
                puesto.getUsuarioModificacion(),
                puesto.getIdPuesto());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM PUESTO WHERE IdPuesto = ?";
        return jdbcTemplate.update(sql, id);
    }
}
