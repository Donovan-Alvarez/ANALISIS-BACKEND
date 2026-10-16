package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** PLANILLA_CABECERA de un periodo ya procesado, con las fechas de PERIODO_PLANILLA. Solo lectura. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportePlanillaCabecera {

    private Integer anio;
    private Integer mes;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalIngresos;
    private BigDecimal totalDescuentos;
    private BigDecimal salarioNeto;
    private LocalDateTime fechaHoraProcesada;
    /** UsuarioCreacion de la cabecera: quien ejecuto Calcular Planilla. */
    private String usuarioProceso;
    private Integer empleados;
}
