package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Respuesta: todo es de solo lectura, incluidos los cuatro totales calculados. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionDTO {

    private Integer idLiquidacion;
    private Integer idEmpleado;
    private String nombrePersona;
    private Integer idPuesto;
    private String nombrePuesto;

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

    private BigDecimal salarioNeto;
    private BigDecimal totalIngresos;
    private BigDecimal totalDescuentos;
    private BigDecimal totalNeto;

    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
}
