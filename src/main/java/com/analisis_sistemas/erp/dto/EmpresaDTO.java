package com.analisis_sistemas.erp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {

    private Integer idEmpresa;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 200, message = "La direccion no puede superar los 200 caracteres")
    private String direccion;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 20, message = "El NIT no puede superar los 20 caracteres")
    private String nit;

    @NotNull(message = "La cantidad de mayusculas es obligatoria")
    @Min(value = 0, message = "La cantidad de mayusculas no puede ser negativa")
    private Integer passwordCantidadMayusculas;

    @NotNull(message = "La cantidad de minusculas es obligatoria")
    @Min(value = 0, message = "La cantidad de minusculas no puede ser negativa")
    private Integer passwordCantidadMinusculas;

    @NotNull(message = "La cantidad de caracteres especiales es obligatoria")
    @Min(value = 0, message = "La cantidad de caracteres especiales no puede ser negativa")
    private Integer passwordCantidadCaracteresEspeciales;

    @NotNull(message = "La cantidad de dias de caducidad es obligatoria")
    @Min(value = 1, message = "La cantidad de dias de caducidad debe ser al menos 1")
    private Integer passwordCantidadCaducidadDias;

    @NotNull(message = "El largo de la contrasena es obligatorio")
    @Min(value = 1, message = "El largo de la contrasena debe ser al menos 1")
    private Integer passwordLargo;

    @NotNull(message = "La cantidad de intentos antes de bloquear es obligatoria")
    @Min(value = 1, message = "La cantidad de intentos antes de bloquear debe ser al menos 1")
    private Integer passwordIntentosAntesDeBloquear;

    @NotNull(message = "La cantidad de numeros es obligatoria")
    @Min(value = 0, message = "La cantidad de numeros no puede ser negativa")
    private Integer passwordCantidadNumeros;

    @NotNull(message = "La cantidad de preguntas a validar es obligatoria")
    @Min(value = 0, message = "La cantidad de preguntas a validar no puede ser negativa")
    private Integer passwordCantidadPreguntasValidar;
}
