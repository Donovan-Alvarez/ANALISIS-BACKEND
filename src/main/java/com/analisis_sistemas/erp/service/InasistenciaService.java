package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.EmpleadoOpcionDTO;
import com.analisis_sistemas.erp.dto.InasistenciaDTO;
import com.analisis_sistemas.erp.entity.Inasistencia;
import com.analisis_sistemas.erp.repository.InasistenciaRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InasistenciaService {

    private static final String MENSAJE_FECHAS =
            "La fecha final no puede ser anterior a la fecha inicial.";
    private static final String MENSAJE_EMPLEADO =
            "El empleado seleccionado no existe o no está activo.";
    private static final String MENSAJE_SOLAPE =
            "El empleado ya tiene una inasistencia registrada que se cruza con esas fechas.";
    private static final String MENSAJE_PROCESADA_CAMBIO =
            "No se puede modificar la inasistencia: ya fue procesada en una planilla.";
    private static final String MENSAJE_PROCESADA_BAJA =
            "No se puede eliminar la inasistencia: ya fue procesada en una planilla.";

    private final InasistenciaRepository inasistenciaRepository;

    public InasistenciaService(InasistenciaRepository inasistenciaRepository) {
        this.inasistenciaRepository = inasistenciaRepository;
    }

    public List<InasistenciaDTO> findAll() {
        return inasistenciaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public InasistenciaDTO findById(Integer id) {
        return toDTO(buscar(id));
    }

    public List<EmpleadoOpcionDTO> findEmpleadosActivos() {
        return inasistenciaRepository.findEmpleadosActivos();
    }

    public InasistenciaDTO create(InasistenciaDTO dto) {
        validarFechas(dto);
        validarEmpleadoActivo(dto.getIdEmpleado());

        if (inasistenciaRepository.existsSolape(dto.getIdEmpleado(), dto.getFechaInicial(), dto.getFechaFinal())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_SOLAPE);
        }

        Inasistencia inasistencia = toEntity(dto);
        inasistencia.setIdInasistencia(null);
        inasistencia.setFechaProcesado(null);
        inasistencia.setFechaCreacion(LocalDateTime.now());
        inasistencia.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Inasistencia guardada = inasistenciaRepository.save(inasistencia);
       
        return toDTO(buscar(guardada.getIdInasistencia()));
    }

    public InasistenciaDTO update(Integer id, InasistenciaDTO dto) {
        Inasistencia existente = buscar(id);

        if (existente.getFechaProcesado() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_PROCESADA_CAMBIO);
        }

        validarFechas(dto);
        validarEmpleadoActivo(dto.getIdEmpleado());

        if (inasistenciaRepository.existsSolapeExcluyendoId(
                dto.getIdEmpleado(), dto.getFechaInicial(), dto.getFechaFinal(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_SOLAPE);
        }

        Inasistencia inasistencia = toEntity(dto);
        inasistencia.setIdInasistencia(id);
        inasistencia.setFechaCreacion(existente.getFechaCreacion());
        inasistencia.setUsuarioCreacion(existente.getUsuarioCreacion());
        inasistencia.setFechaModificacion(LocalDateTime.now());
        inasistencia.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        int filas = inasistenciaRepository.update(inasistencia);
        if (filas == 0) {
            throw noEncontrada(id);
        }
        return toDTO(buscar(id));
    }

   
    public void delete(Integer id) {
        Inasistencia existente = buscar(id);

        if (existente.getFechaProcesado() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_PROCESADA_BAJA);
        }

        int filas = inasistenciaRepository.deleteById(id);
        if (filas == 0) {
            throw noEncontrada(id);
        }
    }

    private void validarFechas(InasistenciaDTO dto) {
        if (dto.getFechaFinal().isBefore(dto.getFechaInicial())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MENSAJE_FECHAS);
        }
    }

    private void validarEmpleadoActivo(Integer idEmpleado) {
        if (!inasistenciaRepository.existsEmpleadoActivo(idEmpleado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MENSAJE_EMPLEADO);
        }
    }

    private Inasistencia buscar(Integer id) {
        return inasistenciaRepository.findById(id).orElseThrow(() -> noEncontrada(id));
    }

    private ResponseStatusException noEncontrada(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Inasistencia no encontrada con id: " + id);
    }

    private InasistenciaDTO toDTO(Inasistencia inasistencia) {
        InasistenciaDTO dto = new InasistenciaDTO();
        dto.setIdInasistencia(inasistencia.getIdInasistencia());
        dto.setIdEmpleado(inasistencia.getIdEmpleado());
        dto.setFechaInicial(inasistencia.getFechaInicial());
        dto.setFechaFinal(inasistencia.getFechaFinal());
        dto.setMotivoInasistencia(inasistencia.getMotivoInasistencia());
        dto.setNombreEmpleado(inasistencia.getNombreEmpleado());
        dto.setNombrePuesto(inasistencia.getNombrePuesto());
        dto.setNombreDepartamento(inasistencia.getNombreDepartamento());
        dto.setFechaProcesado(inasistencia.getFechaProcesado());
        dto.setProcesada(inasistencia.getFechaProcesado() != null);
        if (inasistencia.getFechaInicial() != null && inasistencia.getFechaFinal() != null) {
            dto.setDias((int) ChronoUnit.DAYS.between(inasistencia.getFechaInicial(), inasistencia.getFechaFinal()) + 1);
        }
        return dto;
    }

    private Inasistencia toEntity(InasistenciaDTO dto) {
        Inasistencia inasistencia = new Inasistencia();
        inasistencia.setIdInasistencia(dto.getIdInasistencia());
        inasistencia.setIdEmpleado(dto.getIdEmpleado());
        inasistencia.setFechaInicial(dto.getFechaInicial());
        inasistencia.setFechaFinal(dto.getFechaFinal());
        String motivo = dto.getMotivoInasistencia();
        inasistencia.setMotivoInasistencia(motivo == null || motivo.isBlank() ? null : motivo.trim());
        return inasistencia;
    }
}