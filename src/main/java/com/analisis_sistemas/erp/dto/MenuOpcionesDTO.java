package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuOpcionesDTO {

    private Integer idMenu;
    private String nombre;
    private Integer ordenMenu;
    private List<OpcionPermisoDTO> opciones;
}