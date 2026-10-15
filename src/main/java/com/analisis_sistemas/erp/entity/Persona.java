package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

    private Integer idPersona;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Integer idGenero;
    private String nombreGenero;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private Integer idEstadoCivil;
    private String nombreEstadoCivil;
    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;
}
