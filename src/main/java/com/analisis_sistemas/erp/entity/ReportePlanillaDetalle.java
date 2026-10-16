package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Una fila de PLANILLA_DETALLE tal como la guardo Calcular Planilla, con los
 * nombres del empleado, puesto y departamento (el puesto es el de la planilla,
 * no el actual del empleado). Solo lectura.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportePlanillaDetalle {

    private Integer idPlanillaDetalle;
    private Integer idEmpleado;
    private String nombreEmpleado;
    private String nombrePuesto;
    private String nombreDepartamento;
    private BigDecimal ingresoSueldoBase;
    private BigDecimal ingresoBonificacionDecreto;
    private BigDecimal ingresoOtrosIngresos;
    private BigDecimal totalIngresos;
    private BigDecimal descuentoIgss;
    private BigDecimal descuentoIsr;
    private BigDecimal descuentoInasistencias;
    private BigDecimal totalDescuentos;
    private BigDecimal salarioNeto;
    /** Dias de las inasistencias que este calculo marco como procesadas. */
    private Integer diasInasistencia;
}
