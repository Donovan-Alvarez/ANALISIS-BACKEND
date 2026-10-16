package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.ReportePlanillaDTO;
import com.analisis_sistemas.erp.dto.ReportePlanillaDTO.CabeceraDTO;
import com.analisis_sistemas.erp.dto.ReportePlanillaDTO.DetalleDTO;
import com.analisis_sistemas.erp.dto.ReportePlanillaDTO.PeriodoDTO;
import com.analisis_sistemas.erp.dto.ReportePlanillaDTO.TotalesDTO;
import com.analisis_sistemas.erp.entity.ReportePlanillaCabecera;
import com.analisis_sistemas.erp.entity.ReportePlanillaDetalle;
import com.analisis_sistemas.erp.repository.ReportePlanillaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * Reporte de solo lectura: presenta lo que Calcular Planilla ya guardo y solo
 * agrega (suma) columnas. Todo en BigDecimal para que los totales coincidan al
 * centavo con PLANILLA_CABECERA, sin errores de redondeo de double.
 */
@Service
@Transactional(readOnly = true)
public class ReportePlanillaService {

    private static final String MENSAJE_PERIODO_INVALIDO = "Período inválido.";

    private final ReportePlanillaRepository reporteRepository;

    public ReportePlanillaService(ReportePlanillaRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    public List<PeriodoDTO> findPeriodos() {
        return reporteRepository.findPeriodosProcesados().stream()
                .map(c -> new PeriodoDTO(c.getAnio(), c.getMes(), c.getFechaInicio(), c.getFechaFin(),
                        c.getFechaHoraProcesada(), c.getEmpleados()))
                .toList();
    }

    public ReportePlanillaDTO obtener(int anio, int mes) {
        if (anio < 1 || anio > 9999 || mes < 1 || mes > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MENSAJE_PERIODO_INVALIDO);
        }

        ReportePlanillaCabecera cabecera = reporteRepository.findCabecera(anio, mes)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "La planilla del período " + String.format("%02d/%d", mes, anio)
                                + " todavía no ha sido calculada."));

        List<ReportePlanillaDetalle> filas = reporteRepository.findDetalle(anio, mes);
        TotalesDTO totales = totalizar(filas);

        CabeceraDTO cabeceraDTO = new CabeceraDTO(
                cabecera.getTotalIngresos(), cabecera.getTotalDescuentos(), cabecera.getSalarioNeto());

        return new ReportePlanillaDTO(
                cabecera.getAnio(),
                cabecera.getMes(),
                cabecera.getFechaInicio(),
                cabecera.getFechaFin(),
                cabecera.getFechaHoraProcesada(),
                cabecera.getUsuarioProceso(),
                cabeceraDTO,
                totales,
                cuadra(cabeceraDTO, totales),
                filas.stream().map(this::toDTO).toList());
    }

    private TotalesDTO totalizar(List<ReportePlanillaDetalle> filas) {
        return new TotalesDTO(
                filas.size(),
                sumar(filas, ReportePlanillaDetalle::getIngresoSueldoBase),
                sumar(filas, ReportePlanillaDetalle::getIngresoBonificacionDecreto),
                sumar(filas, ReportePlanillaDetalle::getIngresoOtrosIngresos),
                sumar(filas, ReportePlanillaDetalle::getTotalIngresos),
                sumar(filas, ReportePlanillaDetalle::getDescuentoIgss),
                sumar(filas, ReportePlanillaDetalle::getDescuentoIsr),
                sumar(filas, ReportePlanillaDetalle::getDescuentoInasistencias),
                sumar(filas, ReportePlanillaDetalle::getTotalDescuentos),
                sumar(filas, ReportePlanillaDetalle::getSalarioNeto),
                filas.stream()
                        .map(ReportePlanillaDetalle::getDiasInasistencia)
                        .mapToInt(d -> d == null ? 0 : d)
                        .sum());
    }

    private static BigDecimal sumar(List<ReportePlanillaDetalle> filas,
                                    Function<ReportePlanillaDetalle, BigDecimal> campo) {
        return filas.stream()
                .map(campo)
                .map(v -> v == null ? BigDecimal.ZERO : v)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** compareTo y no equals: 10.5 y 10.50 son el mismo monto aunque tengan distinta escala. */
    private static boolean cuadra(CabeceraDTO cabecera, TotalesDTO totales) {
        return iguales(cabecera.totalIngresos(), totales.totalIngresos())
                && iguales(cabecera.totalDescuentos(), totales.totalDescuentos())
                && iguales(cabecera.salarioNeto(), totales.salarioNeto());
    }

    private static boolean iguales(BigDecimal guardado, BigDecimal sumado) {
        BigDecimal a = guardado == null ? BigDecimal.ZERO : guardado;
        return a.compareTo(sumado) == 0;
    }

    private DetalleDTO toDTO(ReportePlanillaDetalle d) {
        return new DetalleDTO(
                d.getIdPlanillaDetalle(),
                d.getIdEmpleado(),
                d.getNombreEmpleado(),
                d.getNombrePuesto(),
                d.getNombreDepartamento(),
                d.getIngresoSueldoBase(),
                d.getIngresoBonificacionDecreto(),
                d.getIngresoOtrosIngresos(),
                d.getTotalIngresos(),
                d.getDescuentoIgss(),
                d.getDescuentoIsr(),
                d.getDescuentoInasistencias(),
                d.getTotalDescuentos(),
                d.getSalarioNeto(),
                d.getDiasInasistencia());
    }
}
