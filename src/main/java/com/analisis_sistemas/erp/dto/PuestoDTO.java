package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {

    private Integer idPuesto;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    @NotNull(message = "El departamento es obligatorio")
    private Integer idDepartamento;

    /** Solo lectura (respuesta). Si llegan en el cuerpo, se ignoran: la empresa sale del departamento. */
    private String nombreDepartamento;
    private Integer idEmpresa;
    private String nombreEmpresa;
}
