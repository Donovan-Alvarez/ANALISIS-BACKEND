package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoOpcionDTO {

    private Integer idEmpleado;
    private String nombreCompleto;
    private String nombrePuesto;
    private String nombreDepartamento;
}