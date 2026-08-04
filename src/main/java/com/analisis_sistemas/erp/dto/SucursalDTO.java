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
public class SucursalDTO {

    private Integer idSucursal;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 200, message = "La direccion no puede superar los 200 caracteres")
    private String direccion;

    @NotNull(message = "El id de la empresa es obligatorio")
    private Integer idEmpresa;
}
