package com.analisis_sistemas.erp.entity;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class FlujoStatusEmpleado {
 private Integer idStatusActual;
 private Integer idStatusNuevo;
 private String nombreEvento;
 private String statusActual;
 private String statusNuevo;
 private LocalDateTime fechaCreacion;
 private String usuarioCreacion;
 private LocalDateTime fechaModificacion;
 private String usuarioModificacion;
}
