package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancariaEmpleado {

    private Integer idCuentaBancaria;
    private Integer idEmpleado;
    private Integer idBanco;
    private String numeroDeCuenta;
    private Boolean activa;

    private String nombreEmpleado;
    private String nombrePuesto;
    private String nombreBanco;

    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;
}
