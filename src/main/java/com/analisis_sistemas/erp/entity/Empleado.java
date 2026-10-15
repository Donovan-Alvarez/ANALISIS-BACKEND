package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {

    private Integer idEmpleado;
    private Integer idPersona;
    private Integer idSucursal;
    private Integer idPuesto;
    private Integer idStatusEmpleado;
    private LocalDate fechaContratacion;

    // Montos en BigDecimal, no en double: son dinero y no admiten el
    // redondeo binario de coma flotante.
    private BigDecimal ingresoSueldoBase;
    private BigDecimal ingresoBonificacionDecreto;
    private BigDecimal ingresoOtrosIngresos;
    private BigDecimal descuentoIgss;
    private BigDecimal descuentoIsr;
    private BigDecimal descuentoInasistencias;

    /** Solo lectura: vienen de los JOIN de FN_EMPLEADO_LISTAR, nunca se escriben. */
    private String nombrePersona;
    private String nombreSucursal;
    private String nombrePuesto;
    private Integer idDepartamento;
    private String nombreDepartamento;
    private String nombreStatus;

    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;
}
