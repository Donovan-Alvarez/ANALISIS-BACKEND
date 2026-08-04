package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String idUsuario;
    private String nombre;
    private Integer idRole;
    private String nombreRole;
    private Long expiraEn;
}
