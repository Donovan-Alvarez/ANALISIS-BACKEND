package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    private Integer idEmpresa;
    private String nombre;
    private String direccion;
    private String nit;
    private Integer passwordCantidadMayusculas;
    private Integer passwordCantidadMinusculas;
    private Integer passwordCantidadCaracteresEspeciales;
    private Integer passwordCantidadCaducidadDias;
    private Integer passwordLargo;
    private Integer passwordIntentosAntesDeBloquear;
    private Integer passwordCantidadNumeros;
    private Integer passwordCantidadPreguntasValidar;
    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;
}
