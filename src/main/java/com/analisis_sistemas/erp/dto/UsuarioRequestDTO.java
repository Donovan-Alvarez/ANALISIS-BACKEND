package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "El id de usuario es obligatorio")
    @Size(max = 100, message = "El id de usuario no puede superar los 100 caracteres")
    private String idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El status de usuario es obligatorio")
    private Integer idStatusUsuario;

    // Sin Bean Validation: en update() puede venir null/vacio (no se cambia el password existente).
    // La obligatoriedad en create() y la politica de complejidad se validan en el Service.
    private String password;

    @NotNull(message = "El genero es obligatorio")
    private Integer idGenero;

    @Email(message = "El correo electronico no tiene un formato valido")
    @Size(max = 100, message = "El correo electronico no puede superar los 100 caracteres")
    private String correoElectronico;

    private Boolean requiereCambiarPassword;

    private String fotografiaBase64;

    @Size(max = 30, message = "El telefono movil no puede superar los 30 caracteres")
    private String telefonoMovil;

    @NotNull(message = "La sucursal es obligatoria")
    private Integer idSucursal;

    @NotBlank(message = "La pregunta es obligatoria")
    @Size(max = 200, message = "La pregunta no puede superar los 200 caracteres")
    private String pregunta;

    @NotBlank(message = "La respuesta es obligatoria")
    @Size(max = 200, message = "La respuesta no puede superar los 200 caracteres")
    private String respuesta;

    @NotNull(message = "El role es obligatorio")
    private Integer idRole;
}
