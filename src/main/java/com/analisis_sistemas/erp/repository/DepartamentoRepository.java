package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Departamento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class DepartamentoRepository {

    /**
     * Todas las lecturas traen el nombre de la empresa. LEFT JOIN para que un
     * departamento sin empresa (posible antes de 06) no quede oculto.
     */
    private static final String SELECT_BASE = """
            SELECT d.IdDepartamento, d.Nombre, d.IdEmpresa, e.Nombre AS NombreEmpresa,
                   d.FechaCreacion, d.UsuarioCreacion, d.FechaModificacion, d.UsuarioModificacion
            FROM DEPARTAMENTO d
            LEFT JOIN EMPRESA e ON e.IdEmpresa = d.IdEmpresa
            """;

    private final JdbcTemplate jdbcTemplate;
    private final DepartamentoRowMapper departamentoRowMapper;

    public DepartamentoRepository(JdbcTemplate jdbcTemplate, DepartamentoRowMapper departamentoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.departamentoRowMapper = departamentoRowMapper;
    }

    public List<Departamento> findAll() {
        String sql = SELECT_BASE + "ORDER BY e.Nombre, d.Nombre";
        return jdbcTemplate.query(sql, departamentoRowMapper);
    }

    public Optional<Departamento> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE d.IdDepartamento = ?";
        return jdbcTemplate.query(sql, departamentoRowMapper, id).stream().findFirst();
    }

    public List<Departamento> findByEmpresaId(Integer idEmpresa) {
        String sql = SELECT_BASE + "WHERE d.IdEmpresa = ? ORDER BY d.Nombre";
        return jdbcTemplate.query(sql, departamentoRowMapper, idEmpresa);
    }

    /** Duplicado dentro de la misma empresa, sin distinguir mayusculas ni espacios de los extremos (alta). */
    public boolean existsByEmpresaYNombre(Integer idEmpresa, String nombre) {
        String sql = """
                SELECT COUNT(*)
                FROM DEPARTAMENTO
                WHERE IdEmpresa = ?
                  AND UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idEmpresa, nombre);
        return total != null && total > 0;
    }

    /** Igual que {@link #existsByEmpresaYNombre(Integer, String)}, sin contar el propio registro (cambio). */
    public boolean existsByEmpresaYNombreExcluyendoId(Integer idEmpresa, String nombre, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM DEPARTAMENTO
                WHERE IdEmpresa = ?
                  AND UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                  AND IdDepartamento <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idEmpresa, nombre, idExcluir);
        return total != null && total > 0;
    }

    /** Puestos del departamento: impide cambiarle la empresa si tiene alguno. */
    public int countPuestos(Integer idDepartamento) {
        String sql = "SELECT COUNT(*) FROM PUESTO WHERE IdDepartamento = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idDepartamento);
        return total != null ? total : 0;
    }

    public Departamento save(Departamento departamento) {
        String sql = """
                INSERT INTO DEPARTAMENTO (
                    Nombre, IdEmpresa, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDDEPARTAMENTO"});
            ps.setString(1, departamento.getNombre());
            ps.setInt(2, departamento.getIdEmpresa());
            ps.setTimestamp(3, Timestamp.valueOf(departamento.getFechaCreacion()));
            ps.setString(4, departamento.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        departamento.setIdDepartamento(keyHolder.getKey().intValue());
        return departamento;
    }

    public int update(Departamento departamento) {
        String sql = """
                UPDATE DEPARTAMENTO
                SET Nombre = ?, IdEmpresa = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdDepartamento = ?
                """;

        return jdbcTemplate.update(sql,
                departamento.getNombre(),
                departamento.getIdEmpresa(),
                Timestamp.valueOf(departamento.getFechaModificacion()),
                departamento.getUsuarioModificacion(),
                departamento.getIdDepartamento());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM DEPARTAMENTO WHERE IdDepartamento = ?";
        return jdbcTemplate.update(sql, id);
    }
}
