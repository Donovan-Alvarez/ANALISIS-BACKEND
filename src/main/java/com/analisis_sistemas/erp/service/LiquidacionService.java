package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.EmpleadoLiquidableDTO;
import com.analisis_sistemas.erp.dto.LiquidacionDTO;
import com.analisis_sistemas.erp.dto.LiquidacionRequestDTO;
import com.analisis_sistemas.erp.entity.Liquidacion;
import com.analisis_sistemas.erp.repository.LiquidacionRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Solo consulta y alta. No hay update ni delete: una liquidacion emitida es
 * historial permanente del empleado.
 * <p>
 * El Service no calcula nada; la aritmetica vive en PR_LIQUIDACION_CALCULAR.
 * Aqui se validan las fechas para dar mensajes por campo antes de bajar a la
 * base, se resuelve el usuario autenticado para la auditoria, y se relee la
 * fila creada para devolverla con los totales ya calculados.
 */
@Service
public class LiquidacionService {

    private final LiquidacionRepository liquidacionRepository;

    public LiquidacionService(LiquidacionRepository liquidacionRepository) {
        this.liquidacionRepository = liquidacionRepository;
    }

    public List<LiquidacionDTO> findAll() {
        return liquidacionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public LiquidacionDTO findById(Integer id) {
        Liquidacion liquidacion = liquidacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Liquidación no encontrada con id: " + id));
        return toDTO(liquidacion);
    }

    /** Combo de la pantalla: solo empleados sin liquidacion. */
    public List<EmpleadoLiquidableDTO> findEmpleadosLiquidables() {
        return liquidacionRepository.findEmpleadosLiquidables();
    }

    /**
     * Las mismas reglas de fechas estan dentro del procedimiento
     * (ORA-20212 y -20213). Aqui se repiten para poder responder 400 con el
     * campo exacto en vez de un 409 generico; la del procedimiento queda
     * como red de seguridad ante llamadas directas a la base.
     */
    public LiquidacionDTO calcular(LiquidacionRequestDTO dto) {
        if (dto.getFechaLiquidacion().isBefore(dto.getFechaEgreso())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de liquidación no puede ser anterior a la fecha de egreso.");
        }

        if (dto.getFechaEgreso().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de egreso no puede ser futura.");
        }

        Integer idGenerado = liquidacionRepository.calcular(
                dto.getIdEmpleado(),
                dto.getFechaEgreso(),
                dto.getFechaLiquidacion(),
                dto.getMotivoEgreso().trim(),
                LocalDate.now(),
                SecurityUtils.getUsuarioAutenticado());

        // Se relee para devolver los totales tal como quedaron en la base,
        // en vez de recalcularlos en Java y arriesgar que no coincidan.
        return findById(idGenerado);
    }

    private LiquidacionDTO toDTO(Liquidacion liquidacion) {
        LiquidacionDTO dto = new LiquidacionDTO();
        dto.setIdLiquidacion(liquidacion.getIdLiquidacion());
        dto.setIdEmpleado(liquidacion.getIdEmpleado());
        dto.setNombrePersona(liquidacion.getNombrePersona());
        dto.setIdPuesto(liquidacion.getIdPuesto());
        dto.setNombrePuesto(liquidacion.getNombrePuesto());
        dto.setFechaContratacion(liquidacion.getFechaContratacion());
        dto.setFechaEgreso(liquidacion.getFechaEgreso());
        dto.setFechaLiquidacion(liquidacion.getFechaLiquidacion());
        dto.setMotivoEgreso(liquidacion.getMotivoEgreso());
        dto.setIngresoSueldoBase(liquidacion.getIngresoSueldoBase());
        dto.setIngresoBonificacionDecreto(liquidacion.getIngresoBonificacionDecreto());
        dto.setIngresoOtrosIngresos(liquidacion.getIngresoOtrosIngresos());
        dto.setDescuentoIgss(liquidacion.getDescuentoIgss());
        dto.setDescuentoIsr(liquidacion.getDescuentoIsr());
        dto.setDescuentoInasistencias(liquidacion.getDescuentoInasistencias());
        dto.setSalarioNeto(liquidacion.getSalarioNeto());
        dto.setTotalIngresos(liquidacion.getTotalIngresos());
        dto.setTotalDescuentos(liquidacion.getTotalDescuentos());
        dto.setTotalNeto(liquidacion.getTotalNeto());
        dto.setFechaCreacion(liquidacion.getFechaCreacion());
        dto.setUsuarioCreacion(liquidacion.getUsuarioCreacion());
        return dto;
    }
}
