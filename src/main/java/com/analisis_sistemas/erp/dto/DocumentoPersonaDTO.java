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
public class DocumentoPersonaDTO {

    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer idTipoDocumento;

    @NotNull(message = "La persona es obligatoria")
    private Integer idPersona;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número de documento no puede superar los 50 caracteres")
    private String noDocumento;

    private String nombreTipoDocumento;
    private String nombrePersona;
}
