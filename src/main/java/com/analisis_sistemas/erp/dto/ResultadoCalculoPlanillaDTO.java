package com.analisis_sistemas.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoCalculoPlanillaDTO {

    private Integer anio;
    private Integer mes;
    private Integer empleadosProcesados;
    private Integer inasistenciasProcesadas;
    private BigDecimal totalIngresos;
    private BigDecimal totalDescuentos;
    private BigDecimal salarioNeto;
    private LocalDateTime fechaHoraProcesada;
    private String usuario;
}