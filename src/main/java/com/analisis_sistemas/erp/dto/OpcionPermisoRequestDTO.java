package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionPermisoRequestDTO {

    @NotNull(message = "El id de la opcion es obligatorio")
    private Integer idOpcion;

    private boolean consultar;
    private boolean alta;
    private boolean baja;
    private boolean cambio;
    private boolean imprimir;
    private boolean exportar;
}
