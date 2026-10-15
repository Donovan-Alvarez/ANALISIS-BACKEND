package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Lo unico que captura la pantalla. Los montos NO se reciben: el
 * procedimiento los toma del empleado, igual que el puesto, segun el
 * enunciado ("el Service obtiene el Puesto del empleado automaticamente").
 * Asi nadie puede alterar los montos de una liquidacion desde el cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionRequestDTO {

    @NotNull(message = "El empleado es obligatorio")
    private Integer idEmpleado;

    @NotNull(message = "La fecha de egreso es obligatoria")
    private LocalDate fechaEgreso;

    @NotNull(message = "La fecha de liquidación es obligatoria")
    private LocalDate fechaLiquidacion;

    @NotBlank(message = "El motivo de egreso es obligatorio")
    @Size(max = 50, message = "El motivo de egreso no puede superar los 50 caracteres")
    private String motivoEgreso;
}
