package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuloMenuDTO {

    private Integer idModulo;
    private String nombre;
    private Integer ordenMenu;
    private List<MenuOpcionesDTO> menus;
}