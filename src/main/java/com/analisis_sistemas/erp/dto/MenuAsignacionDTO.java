package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuAsignacionDTO {

    private Integer idMenu;
    private String nombre;
    private List<OpcionAsignacionDTO> opciones;
}
