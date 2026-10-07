package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Puesto {

    private Integer idPuesto;
    private String nombre;
    private Integer idDepartamento;
    /** Solo lectura: vienen del JOIN con DEPARTAMENTO y EMPRESA, nunca se escriben. */
    private String nombreDepartamento;
    private Integer idEmpresa;
    private String nombreEmpresa;
    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;
}
