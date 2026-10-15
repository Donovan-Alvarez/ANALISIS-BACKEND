package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {

    private Integer idEmpleado;

    @NotNull(message = "La persona es obligatoria")
    private Integer idPersona;

    @NotNull(message = "La sucursal es obligatoria")
    private Integer idSucursal;

    @NotNull(message = "El puesto es obligatorio")
    private Integer idPuesto;

    @NotNull(message = "El status del empleado es obligatorio")
    private Integer idStatusEmpleado;

    @NotNull(message = "La fecha de contratación es obligatoria")
    @PastOrPresent(message = "La fecha de contratación no puede ser futura")
    private LocalDate fechaContratacion;

    // Los montos tambien se validan en PR_EMPLEADO_VALIDAR_MONTOS: aqui es
    // para dar un mensaje por campo; alla para que no se pueda saltar
    // escribiendo SQL directo.
    @NotNull(message = "El sueldo base es obligatorio")
    @DecimalMin(value = "0.00", message = "El sueldo base no puede ser negativo")
    private BigDecimal ingresoSueldoBase;

    @NotNull(message = "La bonificación decreto es obligatoria")
    @DecimalMin(value = "0.00", message = "La bonificación decreto no puede ser negativa")
    private BigDecimal ingresoBonificacionDecreto;

    @NotNull(message = "Otros ingresos es obligatorio")
    @DecimalMin(value = "0.00", message = "Otros ingresos no puede ser negativo")
    private BigDecimal ingresoOtrosIngresos;

    @NotNull(message = "El descuento de IGSS es obligatorio")
    @DecimalMin(value = "0.00", message = "El descuento de IGSS no puede ser negativo")
    private BigDecimal descuentoIgss;

    @NotNull(message = "El descuento de ISR es obligatorio")
    @DecimalMin(value = "0.00", message = "El descuento de ISR no puede ser negativo")
    private BigDecimal descuentoIsr;

    @NotNull(message = "El descuento por inasistencias es obligatorio")
    @DecimalMin(value = "0.00", message = "El descuento por inasistencias no puede ser negativo")
    private BigDecimal descuentoInasistencias;

    /** Solo lectura (respuesta). Si llegan en el cuerpo, se ignoran. */
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
