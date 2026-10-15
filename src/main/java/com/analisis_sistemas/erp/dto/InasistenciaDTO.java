package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InasistenciaDTO {

    private Integer idInasistencia;

    @NotNull(message = "El empleado es obligatorio")
    private Integer idEmpleado;

    @NotNull(message = "La fecha inicial es obligatoria")
    private LocalDate fechaInicial;

    @NotNull(message = "La fecha final es obligatoria")
    private LocalDate fechaFinal;

    @Size(max = 300, message = "El motivo no puede superar los 300 caracteres")
    private String motivoInasistencia;

   
    private String nombreEmpleado;
    private String nombrePuesto;
    private String nombreDepartamento;
    private Integer dias;
    private LocalDateTime fechaProcesado;
    private Boolean procesada;
}