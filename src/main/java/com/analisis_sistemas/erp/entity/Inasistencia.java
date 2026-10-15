package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inasistencia {

    private Integer idInasistencia;
    private Integer idEmpleado;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private String motivoInasistencia;
   
    private LocalDateTime fechaProcesado;
    private String nombreEmpleado;
    private String nombrePuesto;
    private String nombreDepartamento;
    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;
}