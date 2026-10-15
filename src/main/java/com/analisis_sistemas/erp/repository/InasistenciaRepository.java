package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.dto.EmpleadoOpcionDTO;
import com.analisis_sistemas.erp.entity.Inasistencia;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class InasistenciaRepository {

    private static final String NOMBRE_STATUS_ACTIVO = "Activo";

    private static final String SELECT_BASE = """
            SELECT i.IdInasistencia, i.IdEmpleado,
                   p.Nombre || ' ' || p.Apellido AS NombreEmpleado,
                   pu.Nombre AS NombrePuesto,
                   d.Nombre AS NombreDepartamento,
                   i.FechaInicial, i.FechaFinal, i.MotivoInasistencia, i.FechaProcesado,
                   i.FechaCreacion, i.UsuarioCreacion, i.FechaModificacion, i.UsuarioModificacion
            FROM INASISTENCIA i
            JOIN EMPLEADO e ON e.IdEmpleado = i.IdEmpleado
            JOIN PERSONA p ON p.IdPersona = e.IdPersona
            JOIN PUESTO pu ON pu.IdPuesto = e.IdPuesto
            JOIN DEPARTAMENTO d ON d.IdDepartamento = pu.IdDepartamento
            """;

    private final JdbcTemplate jdbcTemplate;
    private final InasistenciaRowMapper inasistenciaRowMapper;

    public InasistenciaRepository(JdbcTemplate jdbcTemplate, InasistenciaRowMapper inasistenciaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.inasistenciaRowMapper = inasistenciaRowMapper;
    }

    public List<Inasistencia> findAll() {
        String sql = SELECT_BASE + "ORDER BY i.FechaInicial DESC, i.IdInasistencia DESC";
        return jdbcTemplate.query(sql, inasistenciaRowMapper);
    }

    public Optional<Inasistencia> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE i.IdInasistencia = ?";
        return jdbcTemplate.query(sql, inasistenciaRowMapper, id).stream().findFirst();
    }

 
    public List<EmpleadoOpcionDTO> findEmpleadosActivos() {
        String sql = """
                SELECT e.IdEmpleado,
                       p.Nombre || ' ' || p.Apellido AS NombreCompleto,
                       pu.Nombre AS NombrePuesto,
                       d.Nombre AS NombreDepartamento
                FROM EMPLEADO e
                JOIN PERSONA p ON p.IdPersona = e.IdPersona
                JOIN PUESTO pu ON pu.IdPuesto = e.IdPuesto
                JOIN DEPARTAMENTO d ON d.IdDepartamento = pu.IdDepartamento
                JOIN STATUS_EMPLEADO s ON s.IdStatusEmpleado = e.IdStatusEmpleado
                WHERE UPPER(TRIM(s.Nombre)) = UPPER(?)
                ORDER BY p.Nombre, p.Apellido
                """;
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new EmpleadoOpcionDTO(
                        rs.getObject("IDEMPLEADO", Integer.class),
                        rs.getString("NOMBRECOMPLETO"),
                        rs.getString("NOMBREPUESTO"),
                        rs.getString("NOMBREDEPARTAMENTO")),
                NOMBRE_STATUS_ACTIVO);
    }

    public boolean existsEmpleadoActivo(Integer idEmpleado) {
        String sql = """
                SELECT COUNT(*)
                FROM EMPLEADO e
                JOIN STATUS_EMPLEADO s ON s.IdStatusEmpleado = e.IdStatusEmpleado
                WHERE e.IdEmpleado = ?
                  AND UPPER(TRIM(s.Nombre)) = UPPER(?)
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idEmpleado, NOMBRE_STATUS_ACTIVO);
        return total != null && total > 0;
    }

  
    public boolean existsSolape(Integer idEmpleado, LocalDate inicial, LocalDate fin) {
        String sql = """
                SELECT COUNT(*)
                FROM INASISTENCIA
                WHERE IdEmpleado = ?
                  AND FechaInicial <= ?
                  AND FechaFinal >= ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class,
                idEmpleado, Date.valueOf(fin), Date.valueOf(inicial));
        return total != null && total > 0;
    }

   
    public boolean existsSolapeExcluyendoId(Integer idEmpleado, LocalDate inicial, LocalDate fin, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM INASISTENCIA
                WHERE IdEmpleado = ?
                  AND FechaInicial <= ?
                  AND FechaFinal >= ?
                  AND IdInasistencia <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class,
                idEmpleado, Date.valueOf(fin), Date.valueOf(inicial), idExcluir);
        return total != null && total > 0;
    }

    
    public Inasistencia save(Inasistencia inasistencia) {
        String sql = """
                INSERT INTO INASISTENCIA (
                    IdEmpleado, FechaInicial, FechaFinal, MotivoInasistencia, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDINASISTENCIA"});
            ps.setInt(1, inasistencia.getIdEmpleado());
            ps.setDate(2, Date.valueOf(inasistencia.getFechaInicial()));
            ps.setDate(3, Date.valueOf(inasistencia.getFechaFinal()));
            if (inasistencia.getMotivoInasistencia() != null) {
                ps.setString(4, inasistencia.getMotivoInasistencia());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            ps.setTimestamp(5, Timestamp.valueOf(inasistencia.getFechaCreacion()));
            ps.setString(6, inasistencia.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        inasistencia.setIdInasistencia(keyHolder.getKey().intValue());
        return inasistencia;
    }

    
    public int update(Inasistencia inasistencia) {
        String sql = """
                UPDATE INASISTENCIA
                SET IdEmpleado = ?, FechaInicial = ?, FechaFinal = ?, MotivoInasistencia = ?,
                    FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdInasistencia = ?
                """;

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, inasistencia.getIdEmpleado());
            ps.setDate(2, Date.valueOf(inasistencia.getFechaInicial()));
            ps.setDate(3, Date.valueOf(inasistencia.getFechaFinal()));
            if (inasistencia.getMotivoInasistencia() != null) {
                ps.setString(4, inasistencia.getMotivoInasistencia());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            ps.setTimestamp(5, Timestamp.valueOf(inasistencia.getFechaModificacion()));
            ps.setString(6, inasistencia.getUsuarioModificacion());
            ps.setInt(7, inasistencia.getIdInasistencia());
            return ps;
        });
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM INASISTENCIA WHERE IdInasistencia = ?";
        return jdbcTemplate.update(sql, id);
    }
}