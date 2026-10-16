package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.ReportePlanillaDTO;
import com.analisis_sistemas.erp.dto.ReportePlanillaDTO.PeriodoDTO;
import com.analisis_sistemas.erp.service.ReportePlanillaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Solo GET: el reporte no da de alta, cambia ni borra nada. Slug 'reporte-planilla'. */
@RestController
@RequestMapping("/api/reporte-planilla")
public class ReportePlanillaController {

    private final ReportePlanillaService reporteService;

    public ReportePlanillaController(ReportePlanillaService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/periodos")
    public ResponseEntity<List<PeriodoDTO>> findPeriodos() {
        return ResponseEntity.ok(reporteService.findPeriodos());
    }

    @GetMapping("/{anio}/{mes}")
    public ResponseEntity<ReportePlanillaDTO> obtener(@PathVariable int anio, @PathVariable int mes) {
        return ResponseEntity.ok(reporteService.obtener(anio, mes));
    }
}
