package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.CalculoPlanillaRequestDTO;
import com.analisis_sistemas.erp.dto.PeriodoPlanillaDTO;
import com.analisis_sistemas.erp.dto.ResultadoCalculoPlanillaDTO;
import com.analisis_sistemas.erp.entity.PeriodoPlanilla;
import com.analisis_sistemas.erp.repository.CalculoPlanillaRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CalculoPlanillaService {

    private static final String[] MESES = {
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    };
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CalculoPlanillaRepository calculoPlanillaRepository;

    public CalculoPlanillaService(CalculoPlanillaRepository calculoPlanillaRepository) {
        this.calculoPlanillaRepository = calculoPlanillaRepository;
    }

    public List<PeriodoPlanillaDTO> findPeriodos() {
        return calculoPlanillaRepository.findPeriodos().stream()
                .map(this::toDTO)
                .toList();
    }

  
    public ResultadoCalculoPlanillaDTO calcular(CalculoPlanillaRequestDTO dto) {
        PeriodoPlanilla periodo = calculoPlanillaRepository.findPeriodo(dto.getAnio(), dto.getMes())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El período seleccionado no existe."));

        if (periodo.getFechaHoraProcesada() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La planilla de " + etiqueta(periodo) + " ya fue calculada el "
                            + periodo.getFechaHoraProcesada().format(FORMATO_FECHA_HORA)
                            + ". No se puede calcular dos veces.");
        }

        return calculoPlanillaRepository.calcular(
                dto.getAnio(), dto.getMes(), SecurityUtils.getUsuarioAutenticado(), LocalDateTime.now());
    }

    private String etiqueta(PeriodoPlanilla periodo) {
        return MESES[periodo.getMes() - 1] + " de " + periodo.getAnio();
    }

    private PeriodoPlanillaDTO toDTO(PeriodoPlanilla periodo) {
        PeriodoPlanillaDTO dto = new PeriodoPlanillaDTO();
        dto.setAnio(periodo.getAnio());
        dto.setMes(periodo.getMes());
        dto.setFechaInicio(periodo.getFechaInicio());
        dto.setFechaFin(periodo.getFechaFin());
        dto.setCalculado(periodo.getFechaHoraProcesada() != null);
        dto.setTotalIngresos(periodo.getTotalIngresos());
        dto.setTotalDescuentos(periodo.getTotalDescuentos());
        dto.setSalarioNeto(periodo.getSalarioNeto());
        dto.setFechaHoraProcesada(periodo.getFechaHoraProcesada());
        dto.setUsuarioProceso(periodo.getUsuarioProceso());
        dto.setEmpleadosCalculados(periodo.getEmpleadosCalculados());
        return dto;
    }
}