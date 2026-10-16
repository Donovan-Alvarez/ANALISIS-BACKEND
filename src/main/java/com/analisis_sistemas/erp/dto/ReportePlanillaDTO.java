package com.analisis_sistemas.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GET /api/reporte-planilla/{anio}/{mes}.
 *
 * cabecera = lo guardado en PLANILLA_CABECERA; totales = suma de las filas de
 * PLANILLA_DETALLE. cuadraConCabecera indica si coinciden al centavo (deben
 * coincidir siempre; si no, la planilla tiene un problema que revisar).
 */
public record ReportePlanillaDTO(
        Integer anio,
        Integer mes,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDateTime fechaHoraProcesada,
        String usuarioProceso,
        CabeceraDTO cabecera,
        TotalesDTO totales,
        boolean cuadraConCabecera,
        List<DetalleDTO> detalle) {

    public record CabeceraDTO(BigDecimal totalIngresos, BigDecimal totalDescuentos, BigDecimal salarioNeto) {
    }

    public record TotalesDTO(
            Integer empleados,
            BigDecimal ingresoSueldoBase,
            BigDecimal ingresoBonificacionDecreto,
            BigDecimal ingresoOtrosIngresos,
            BigDecimal totalIngresos,
            BigDecimal descuentoIgss,
            BigDecimal descuentoIsr,
            BigDecimal descuentoInasistencias,
            BigDecimal totalDescuentos,
            BigDecimal salarioNeto,
            Integer diasInasistencia) {
    }

    public record DetalleDTO(
            Integer idPlanillaDetalle,
            Integer idEmpleado,
            String nombreEmpleado,
            String nombrePuesto,
            String nombreDepartamento,
            BigDecimal ingresoSueldoBase,
            BigDecimal ingresoBonificacionDecreto,
            BigDecimal ingresoOtrosIngresos,
            BigDecimal totalIngresos,
            BigDecimal descuentoIgss,
            BigDecimal descuentoIsr,
            BigDecimal descuentoInasistencias,
            BigDecimal totalDescuentos,
            BigDecimal salarioNeto,
            Integer diasInasistencia) {
    }

    /** GET /api/reporte-planilla/periodos: opciones del combo de Periodo. */
    public record PeriodoDTO(
            Integer anio,
            Integer mes,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            LocalDateTime fechaHoraProcesada,
            Integer empleados) {
    }
}
