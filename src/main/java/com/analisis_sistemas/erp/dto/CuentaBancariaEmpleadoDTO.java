package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancariaEmpleadoDTO {

    private Integer idCuentaBancaria;

    @NotNull(message = "El empleado es obligatorio")
    private Integer idEmpleado;

    @NotNull(message = "El banco es obligatorio")
    private Integer idBanco;

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(min = 5, max = 50, message = "El número de cuenta debe tener entre 5 y 50 caracteres")
    @Pattern(regexp = "^\\s*\\d+(-\\d+)*\\s*$",
            message = "El número de cuenta solo admite dígitos separados por guiones (ej. 001-438715)")
    private String numeroDeCuenta;

    private Boolean activa;

    private String nombreEmpleado;
    private String nombrePuesto;
    private String nombreBanco;
}
