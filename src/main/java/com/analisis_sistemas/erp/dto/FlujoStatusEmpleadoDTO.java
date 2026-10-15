package com.analisis_sistemas.erp.dto;
import lombok.Data;
import jakarta.validation.constraints.*;
@Data
public class FlujoStatusEmpleadoDTO {
 @NotNull @Positive private Integer idStatusActual;
 @NotNull @Positive private Integer idStatusNuevo;
 @NotBlank @Size(max=50) private String nombreEvento;
 private String statusActual;
 private String statusNuevo;
}
