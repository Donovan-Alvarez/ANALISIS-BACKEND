package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordRequestDTO {

    @NotBlank(message = "El token es obligatorio")
    private String idToken;

    @NotBlank(message = "El password nuevo es obligatorio")
    private String passwordNuevo;
}
