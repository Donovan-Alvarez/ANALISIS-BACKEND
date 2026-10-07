package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Departamento {

    private Integer idDepartamento;
    private String nombre;
    private Integer idEmpresa;
    /** Solo lectura: viene del JOIN con EMPRESA, nunca se escribe. */
    private String nombreEmpresa;
    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;
}
