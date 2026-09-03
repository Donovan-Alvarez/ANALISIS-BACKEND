package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionAsignacionDTO {

    private Integer idOpcion;
    private String nombre;

    private boolean consultar;
    private boolean alta;
    private boolean baja;
    private boolean cambio;
    private boolean imprimir;
    private boolean exportar;
}
