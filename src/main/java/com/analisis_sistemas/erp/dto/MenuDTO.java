package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {

    private Integer idMenu;

    @NotNull(message = "El id del modulo es obligatorio")
    private Integer idModulo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    @NotNull(message = "El orden de menu es obligatorio")
    @Min(value = 0, message = "El orden de menu no puede ser negativo")
    private Integer ordenMenu;
}
