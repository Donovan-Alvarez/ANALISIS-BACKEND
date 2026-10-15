package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.CalculoPlanillaRequestDTO;
import com.analisis_sistemas.erp.dto.PeriodoPlanillaDTO;
import com.analisis_sistemas.erp.dto.ResultadoCalculoPlanillaDTO;
import com.analisis_sistemas.erp.service.CalculoPlanillaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/calculo-planilla")
public class CalculoPlanillaController {

    private final CalculoPlanillaService calculoPlanillaService;

    public CalculoPlanillaController(CalculoPlanillaService calculoPlanillaService) {
        this.calculoPlanillaService = calculoPlanillaService;
    }

    @GetMapping("/periodos")
    public ResponseEntity<List<PeriodoPlanillaDTO>> findPeriodos() {
        return ResponseEntity.ok(calculoPlanillaService.findPeriodos());
    }

    @PostMapping("/calcular")
    public ResponseEntity<ResultadoCalculoPlanillaDTO> calcular(@Valid @RequestBody CalculoPlanillaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calculoPlanillaService.calcular(dto));
    }
}