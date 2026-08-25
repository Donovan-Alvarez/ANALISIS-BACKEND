package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleOpcionesResponseDTO {

    private Integer idRole;
    private Integer idModulo;
    private List<MenuAsignacionDTO> menus;
}
