package com.analisis_sistemas.erp.entity;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class BoletaPago {
 private Integer idPlanillaDetalle;
 private Integer anio;
 private Integer mes;
 private Integer idEmpleado;
 private String empleado;
 private String puesto;
 private LocalDate fechaContratacion;
 private BigDecimal ingresoSueldoBase;
 private BigDecimal ingresoBonificacionDecreto;
 private BigDecimal ingresoOtrosIngresos;
 private BigDecimal descuentoIgss;
 private BigDecimal descuentoIsr;
 private BigDecimal descuentoInasistencias;
 private BigDecimal salarioNeto;
}
