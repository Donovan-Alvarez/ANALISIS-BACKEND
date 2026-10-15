package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Empleado que todavia puede liquidarse (no tiene liquidacion registrada).
 * <p>
 * Alimenta el combo de la pantalla de Liquidacion y, de paso, trae el puesto
 * y los montos para que el formulario los muestre en solo lectura antes de
 * calcular: asi el usuario ve con que datos se va a liquidar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoLiquidableDTO {

    private Integer idEmpleado;
    private String nombrePersona;
    private Integer idPuesto;
    private String nombrePuesto;
    private LocalDate fechaContratacion;

    private BigDecimal ingresoSueldoBase;
    private BigDecimal ingresoBonificacionDecreto;
    private BigDecimal ingresoOtrosIngresos;
    private BigDecimal descuentoIgss;
    private BigDecimal descuentoIsr;
    private BigDecimal descuentoInasistencias;
}
