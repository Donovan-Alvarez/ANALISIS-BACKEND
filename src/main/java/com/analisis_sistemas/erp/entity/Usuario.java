package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private String idUsuario;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Integer idStatusUsuario;
    private String password;
    private Integer idGenero;
    private LocalDateTime ultimaFechaIngreso;
    private Integer intentosDeAcceso;
    private String sesionActual;
    private LocalDateTime ultimaFechaCambioPassword;
    private String correoElectronico;
    private Boolean requiereCambiarPassword;
    private byte[] fotografia;
    private String telefonoMovil;
    private Integer idSucursal;
    private String pregunta;
    private String respuesta;
    private Integer idRole;
    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;
}
