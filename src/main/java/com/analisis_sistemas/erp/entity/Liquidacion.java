package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Historial permanente de la liquidacion de un empleado.
 * <p>
 * Los montos son una fotografia del momento del egreso: se copian del
 * empleado al calcular y ya no se mueven, aunque despues le cambien el
 * sueldo. Por eso la tabla no tiene FechaModificacion ni
 * UsuarioModificacion: una liquidacion emitida no se edita.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Liquidacion {

    private Integer idLiquidacion;
    private Integer idEmpleado;
    private Integer idPuesto;

    private LocalDate fechaContratacion;
    private LocalDate fechaEgreso;
    private LocalDate fechaLiquidacion;
    private String motivoEgreso;

    private BigDecimal ingresoSueldoBase;
    private BigDecimal ingresoBonificacionDecreto;
    private BigDecimal ingresoOtrosIngresos;
    private BigDecimal descuentoIgss;
    private BigDecimal descuentoIsr;
    private BigDecimal descuentoInasistencias;

    // Calculados por PR_LIQUIDACION_CALCULAR.
    private BigDecimal salarioNeto;
    private BigDecimal totalIngresos;
    private BigDecimal totalDescuentos;
    private BigDecimal totalNeto;

    /** Solo lectura: vienen de los JOIN con PERSONA y PUESTO. */
    private String nombrePersona;
    private String nombrePuesto;

    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
}
