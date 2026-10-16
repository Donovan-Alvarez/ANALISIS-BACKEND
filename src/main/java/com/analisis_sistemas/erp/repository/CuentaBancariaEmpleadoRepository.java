package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.dto.EmpleadoOpcionDTO;
import com.analisis_sistemas.erp.entity.CuentaBancariaEmpleado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class CuentaBancariaEmpleadoRepository {

    private static final String SELECT_BASE = """
            SELECT cb.IdCuentaBancaria, cb.IdEmpleado, cb.IdBanco, cb.NumeroDeCuenta, cb.Activa,
                   p.Nombre || ' ' || p.Apellido AS NombreEmpleado,
                   pu.Nombre AS NombrePuesto,
                   b.Nombre AS NombreBanco,
                   cb.FechaCreacion, cb.UsuarioCreacion, cb.FechaModificacion, cb.UsuarioModificacion
            FROM CUENTA_BANCARIA_EMPLEADO cb
            JOIN EMPLEADO e ON e.IdEmpleado = cb.IdEmpleado
            JOIN PERSONA p ON p.IdPersona = e.IdPersona
            JOIN PUESTO pu ON pu.IdPuesto = e.IdPuesto
            JOIN BANCO b ON b.IdBanco = cb.IdBanco
            """;

    private final JdbcTemplate jdbcTemplate;
    private final CuentaBancariaEmpleadoRowMapper cuentaRowMapper;

    public CuentaBancariaEmpleadoRepository(JdbcTemplate jdbcTemplate, CuentaBancariaEmpleadoRowMapper cuentaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.cuentaRowMapper = cuentaRowMapper;
    }

    /**
     * Lista con filtros opcionales, siempre con los mismos parametros:
     * soloActivas = false trae todas; idEmpleado = null trae las de todos.
     * Es el contrato que usa Boletas de Pago (soloActivas + idEmpleado).
     */
    public List<CuentaBancariaEmpleado> findAll(boolean soloActivas, Integer idEmpleado) {
        String sql = SELECT_BASE + """
                WHERE (? = 0 OR cb.Activa = 1)
                  AND (? = 0 OR cb.IdEmpleado = ?)
                ORDER BY p.Nombre, p.Apellido, b.Nombre, cb.IdCuentaBancaria
                """;
        int filtrarEmpleado = idEmpleado != null ? 1 : 0;
        int empleado = idEmpleado != null ? idEmpleado : 0;
        return jdbcTemplate.query(sql, cuentaRowMapper, soloActivas ? 1 : 0, filtrarEmpleado, empleado);
    }

    public Optional<CuentaBancariaEmpleado> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE cb.IdCuentaBancaria = ?";
        return jdbcTemplate.query(sql, cuentaRowMapper, id).stream().findFirst();
    }

    /** Opciones del combo de Empleado: todos los empleados (no solo los activos, para poder editar cuentas viejas). */
    public List<EmpleadoOpcionDTO> findEmpleados() {
        String sql = """
                SELECT e.IdEmpleado,
                       p.Nombre || ' ' || p.Apellido AS NombreCompleto,
                       pu.Nombre AS NombrePuesto,
                       d.Nombre AS NombreDepartamento
                FROM EMPLEADO e
                JOIN PERSONA p ON p.IdPersona = e.IdPersona
                JOIN PUESTO pu ON pu.IdPuesto = e.IdPuesto
                JOIN DEPARTAMENTO d ON d.IdDepartamento = pu.IdDepartamento
                ORDER BY p.Nombre, p.Apellido
                """;
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new EmpleadoOpcionDTO(
                        rs.getObject("IDEMPLEADO", Integer.class),
                        rs.getString("NOMBRECOMPLETO"),
                        rs.getString("NOMBREPUESTO"),
                        rs.getString("NOMBREDEPARTAMENTO")));
    }

    public boolean existsEmpleado(Integer idEmpleado) {
        String sql = "SELECT COUNT(*) FROM EMPLEADO WHERE IdEmpleado = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idEmpleado);
        return total != null && total > 0;
    }

    /**
     * El numero ya existe en ese banco, sin contar la propia cuenta.
     * En el alta se pasa idExcluir = 0 (los IDENTITY empiezan en 1).
     */
    public boolean existsNumeroEnBanco(Integer idBanco, String numeroDeCuenta, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM CUENTA_BANCARIA_EMPLEADO
                WHERE IdBanco = ?
                  AND UPPER(TRIM(NumeroDeCuenta)) = UPPER(TRIM(?))
                  AND IdCuentaBancaria <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idBanco, numeroDeCuenta, idExcluir);
        return total != null && total > 0;
    }

    /** El empleado ya tiene otra cuenta activa en ese banco (idExcluir = 0 en el alta). */
    public boolean existsActivaEnBanco(Integer idEmpleado, Integer idBanco, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM CUENTA_BANCARIA_EMPLEADO
                WHERE IdEmpleado = ?
                  AND IdBanco = ?
                  AND Activa = 1
                  AND IdCuentaBancaria <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idEmpleado, idBanco, idExcluir);
        return total != null && total > 0;
    }

    public CuentaBancariaEmpleado save(CuentaBancariaEmpleado cuenta) {
        String sql = """
                INSERT INTO CUENTA_BANCARIA_EMPLEADO (
                    IdEmpleado, IdBanco, NumeroDeCuenta, Activa, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDCUENTABANCARIA"});
            ps.setInt(1, cuenta.getIdEmpleado());
            ps.setInt(2, cuenta.getIdBanco());
            ps.setString(3, cuenta.getNumeroDeCuenta());
            ps.setInt(4, Boolean.TRUE.equals(cuenta.getActiva()) ? 1 : 0);
            ps.setTimestamp(5, Timestamp.valueOf(cuenta.getFechaCreacion()));
            ps.setString(6, cuenta.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        cuenta.setIdCuentaBancaria(keyHolder.getKey().intValue());
        return cuenta;
    }

    public int update(CuentaBancariaEmpleado cuenta) {
        String sql = """
                UPDATE CUENTA_BANCARIA_EMPLEADO
                SET IdEmpleado = ?, IdBanco = ?, NumeroDeCuenta = ?, Activa = ?,
                    FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdCuentaBancaria = ?
                """;

        return jdbcTemplate.update(sql,
                cuenta.getIdEmpleado(),
                cuenta.getIdBanco(),
                cuenta.getNumeroDeCuenta(),
                Boolean.TRUE.equals(cuenta.getActiva()) ? 1 : 0,
                Timestamp.valueOf(cuenta.getFechaModificacion()),
                cuenta.getUsuarioModificacion(),
                cuenta.getIdCuentaBancaria());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM CUENTA_BANCARIA_EMPLEADO WHERE IdCuentaBancaria = ?";
        return jdbcTemplate.update(sql, id);
    }
}
