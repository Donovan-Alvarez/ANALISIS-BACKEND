package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarRespuestaRequestDTO {

    @NotBlank(message = "El id de usuario es obligatorio")
    private String idUsuario;

    @NotBlank(message = "La respuesta es obligatoria")
    private String respuesta;
}
