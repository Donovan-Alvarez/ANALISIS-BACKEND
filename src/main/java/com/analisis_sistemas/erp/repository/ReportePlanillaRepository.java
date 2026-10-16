package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.ReportePlanillaCabecera;
import com.analisis_sistemas.erp.entity.ReportePlanillaDetalle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Solo SELECT sobre lo que Calcular Planilla ya guardo: nunca recalcula
 * sueldos ni descuentos. Los "totales" de cada fila son la suma de las
 * columnas almacenadas, con la misma definicion que usa PR_CALCULAR_PLANILLA
 * para llenar PLANILLA_CABECERA.
 */
@Repository
public class ReportePlanillaRepository {

    private static final String SELECT_CABECERA = """
            SELECT c.Anio, c.Mes, pp.FechaInicio, pp.FechaFin,
                   c.TotalIngresos, c.TotalDescuentos, c.SalarioNeto,
                   c.FechaHoraProcesada, c.UsuarioCreacion AS UsuarioProceso,
                   (SELECT COUNT(*) FROM PLANILLA_DETALLE d
                     WHERE d.Anio = c.Anio AND d.Mes = c.Mes) AS Empleados
            FROM PLANILLA_CABECERA c
            JOIN PERIODO_PLANILLA pp ON pp.Anio = c.Anio AND pp.Mes = c.Mes
            WHERE c.FechaHoraProcesada IS NOT NULL
            """;

    private final JdbcTemplate jdbcTemplate;
    private final ReportePlanillaCabeceraRowMapper cabeceraRowMapper;
    private final ReportePlanillaDetalleRowMapper detalleRowMapper;

    public ReportePlanillaRepository(JdbcTemplate jdbcTemplate,
                                     ReportePlanillaCabeceraRowMapper cabeceraRowMapper,
                                     ReportePlanillaDetalleRowMapper detalleRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.cabeceraRowMapper = cabeceraRowMapper;
        this.detalleRowMapper = detalleRowMapper;
    }

    /** Periodos con planilla procesada, del mas reciente al mas antiguo. */
    public List<ReportePlanillaCabecera> findPeriodosProcesados() {
        String sql = SELECT_CABECERA + "ORDER BY c.Anio DESC, c.Mes DESC";
        return jdbcTemplate.query(sql, cabeceraRowMapper);
    }

    public Optional<ReportePlanillaCabecera> findCabecera(int anio, int mes) {
        String sql = SELECT_CABECERA + "AND c.Anio = ? AND c.Mes = ?";
        return jdbcTemplate.query(sql, cabeceraRowMapper, anio, mes).stream().findFirst();
    }

    /**
     * DiasInasistencia: dias de las inasistencias que este mismo calculo marco
     * como procesadas (el SP les pone FechaProcesado = FechaHoraProcesada de la
     * cabecera). Es informativo; el monto descontado es DescuentoInasistencias.
     */
    public List<ReportePlanillaDetalle> findDetalle(int anio, int mes) {
        String sql = """
                SELECT d.IdPlanillaDetalle, d.IdEmpleado,
                       p.Nombre || ' ' || p.Apellido AS NombreEmpleado,
                       pu.Nombre AS NombrePuesto,
                       dep.Nombre AS NombreDepartamento,
                       d.IngresoSueldoBase, d.IngresoBonificacionDecreto, d.IngresoOtrosIngresos,
                       d.IngresoSueldoBase + d.IngresoBonificacionDecreto + d.IngresoOtrosIngresos AS TotalIngresos,
                       d.DescuentoIgss, d.DescuentoIsr, d.DescuentoInasistencias,
                       d.DescuentoIgss + d.DescuentoIsr + d.DescuentoInasistencias AS TotalDescuentos,
                       d.SalarioNeto,
                       (SELECT NVL(SUM(TRUNC(i.FechaFinal) - TRUNC(i.FechaInicial) + 1), 0)
                          FROM INASISTENCIA i
                         WHERE i.IdEmpleado = d.IdEmpleado
                           AND i.FechaProcesado = c.FechaHoraProcesada) AS DiasInasistencia
                FROM PLANILLA_DETALLE d
                JOIN PLANILLA_CABECERA c ON c.Anio = d.Anio AND c.Mes = d.Mes
                JOIN EMPLEADO e ON e.IdEmpleado = d.IdEmpleado
                JOIN PERSONA p ON p.IdPersona = e.IdPersona
                JOIN PUESTO pu ON pu.IdPuesto = d.IdPuesto
                JOIN DEPARTAMENTO dep ON dep.IdDepartamento = pu.IdDepartamento
                WHERE d.Anio = ? AND d.Mes = ?
                ORDER BY dep.Nombre, p.Nombre, p.Apellido
                """;
        return jdbcTemplate.query(sql, detalleRowMapper, anio, mes);
    }
}
