package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cambio de password del propio usuario autenticado (flujo de cambio
 * obligatorio tras login, RequiereCambiarPassword = 1). No requiere token de
 * recuperacion: el usuario ya demostro su identidad con el JWT vigente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordPropioRequestDTO {

    @NotBlank(message = "El password nuevo es obligatorio")
    private String passwordNuevo;
}
