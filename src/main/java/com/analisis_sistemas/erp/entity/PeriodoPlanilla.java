package com.analisis_sistemas.erp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoPlanilla {

    private Integer anio;
    private Integer mes;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalIngresos;
    private BigDecimal totalDescuentos;
    private BigDecimal salarioNeto;
    private LocalDateTime fechaHoraProcesada;
    private String usuarioProceso;
    private Integer empleadosCalculados;
}