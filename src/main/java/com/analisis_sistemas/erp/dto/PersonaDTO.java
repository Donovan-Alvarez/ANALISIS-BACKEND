package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDTO {

    public static final String REGEX_NOMBRE = "^\\s*\\p{L}[\\p{L} '.-]*\\s*$";

    public static final String REGEX_CORREO =
            "^$|^\\s*[A-Za-z0-9]+([._%+-][A-Za-z0-9]+)*@([A-Za-z0-9]+(-[A-Za-z0-9]+)*\\.)+[A-Za-z]{2,}\\s*$";

    public static final String REGEX_TELEFONO = "^\\s*\\+?[0-9][0-9 -]{6,18}[0-9]\\s*$";

    private Integer idPersona;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    @Pattern(regexp = REGEX_NOMBRE, message = "El nombre solo puede contener letras, espacios, apóstrofe, punto y guion")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
    @Pattern(regexp = REGEX_NOMBRE, message = "El apellido solo puede contener letras, espacios, apóstrofe, punto y guion")
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El género es obligatorio")
    private Integer idGenero;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 100, message = "La dirección no puede superar los 100 caracteres")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 50, message = "El teléfono no puede superar los 50 caracteres")
    @Pattern(regexp = REGEX_TELEFONO, message = "El teléfono solo puede llevar números, espacios y guiones (con + opcional al inicio)")
    private String telefono;

    @Size(max = 50, message = "El correo no puede superar los 50 caracteres")
    @Pattern(regexp = REGEX_CORREO, message = "El correo electrónico no tiene un formato válido (ej. nombre@empresa.com)")
    private String correoElectronico;

    @NotNull(message = "El estado civil es obligatorio")
    private Integer idEstadoCivil;

    private String nombreGenero;
    private String nombreEstadoCivil;
    private String nombreCompleto;
}
