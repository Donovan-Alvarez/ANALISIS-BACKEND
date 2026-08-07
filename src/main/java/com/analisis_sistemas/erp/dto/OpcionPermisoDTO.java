package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionPermisoDTO {

    private Integer idOpcion;
    private String nombre;
    private Integer ordenMenu;
    private String pagina;
    private boolean alta;
    private boolean baja;
    private boolean cambio;
    private boolean imprimir;
    private boolean exportar;
}