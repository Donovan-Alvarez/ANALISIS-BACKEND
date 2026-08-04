package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraAcceso {

    private Integer idBitacoraAcceso;
    private String idUsuario;
    private Integer idTipoAcceso;
    private LocalDateTime fechaAcceso;
    private String httpUserAgent;
    private String direccionIp;
    private String acceso;
    private String sesion;
    private String sistemaOperativo;
    private String dispositivo;
    private String browser;
}
