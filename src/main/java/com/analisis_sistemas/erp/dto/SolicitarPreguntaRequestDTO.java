package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitarPreguntaRequestDTO {

    @NotBlank(message = "El id de usuario es obligatorio")
    private String idUsuario;
}
