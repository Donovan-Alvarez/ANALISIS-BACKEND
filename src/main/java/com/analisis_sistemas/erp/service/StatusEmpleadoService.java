package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.StatusEmpleadoDTO;
import com.analisis_sistemas.erp.entity.StatusEmpleado;
import com.analisis_sistemas.erp.repository.StatusEmpleadoRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusEmpleadoService {

    private static final String MENSAJE_DUPLICADO = "Ya existe un status de empleado con ese nombre.";

    private final StatusEmpleadoRepository statusEmpleadoRepository;

    public StatusEmpleadoService(StatusEmpleadoRepository statusEmpleadoRepository) {
        this.statusEmpleadoRepository = statusEmpleadoRepository;
    }

    public List<StatusEmpleadoDTO> findAll() {
        return statusEmpleadoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public StatusEmpleadoDTO findById(Integer id) {
        StatusEmpleado statusEmpleado = statusEmpleadoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
        return toDTO(statusEmpleado);
    }

    public StatusEmpleadoDTO create(StatusEmpleadoDTO dto) {
        String nombre = dto.getNombre().trim();
        if (statusEmpleadoRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        StatusEmpleado statusEmpleado = toEntity(dto);
        statusEmpleado.setIdStatusEmpleado(null);
        statusEmpleado.setNombre(nombre);
        statusEmpleado.setFechaCreacion(LocalDateTime.now());
        statusEmpleado.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        StatusEmpleado saved = statusEmpleadoRepository.save(statusEmpleado);
        return toDTO(saved);
    }

    public StatusEmpleadoDTO update(Integer id, StatusEmpleadoDTO dto) {
        StatusEmpleado existente = statusEmpleadoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));

        String nombre = dto.getNombre().trim();
        if (statusEmpleadoRepository.existsByNombreExcluyendoId(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        StatusEmpleado statusEmpleado = toEntity(dto);
        statusEmpleado.setIdStatusEmpleado(id);
        statusEmpleado.setNombre(nombre);
        statusEmpleado.setFechaCreacion(existente.getFechaCreacion());
        statusEmpleado.setUsuarioCreacion(existente.getUsuarioCreacion());
        statusEmpleado.setFechaModificacion(LocalDateTime.now());
        statusEmpleado.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        statusEmpleadoRepository.update(statusEmpleado);
        return toDTO(statusEmpleado);
    }

    /**
     * Si el status de empleado esta asignado a empleados, se usa en flujos de status o aparece
     * en detalles de planilla, TRG_STATUS_EMPLEADO_BAJA_VALIDA
     * (ORA-20103/-20104/-20105) rechaza el DELETE y GlobalExceptionHandler responde 409 con
     * el mensaje del trigger; aqui no se captura nada (D2).
     */
    public void delete(Integer id) {
        int filasAfectadas = statusEmpleadoRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw noEncontrado(id);
        }
    }

    private ResponseStatusException noEncontrado(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Status de empleado no encontrado con id: " + id);
    }

    private StatusEmpleadoDTO toDTO(StatusEmpleado statusEmpleado) {
        StatusEmpleadoDTO dto = new StatusEmpleadoDTO();
        dto.setIdStatusEmpleado(statusEmpleado.getIdStatusEmpleado());
        dto.setNombre(statusEmpleado.getNombre());
        return dto;
    }

    private StatusEmpleado toEntity(StatusEmpleadoDTO dto) {
        StatusEmpleado statusEmpleado = new StatusEmpleado();
        statusEmpleado.setIdStatusEmpleado(dto.getIdStatusEmpleado());
        statusEmpleado.setNombre(dto.getNombre());
        return statusEmpleado;
    }
}
