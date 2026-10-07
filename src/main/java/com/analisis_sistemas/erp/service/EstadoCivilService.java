package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.EstadoCivilDTO;
import com.analisis_sistemas.erp.entity.EstadoCivil;
import com.analisis_sistemas.erp.repository.EstadoCivilRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EstadoCivilService {

    private static final String MENSAJE_DUPLICADO = "Ya existe un estado civil con ese nombre.";

    private final EstadoCivilRepository estadoCivilRepository;

    public EstadoCivilService(EstadoCivilRepository estadoCivilRepository) {
        this.estadoCivilRepository = estadoCivilRepository;
    }

    public List<EstadoCivilDTO> findAll() {
        return estadoCivilRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public EstadoCivilDTO findById(Integer id) {
        EstadoCivil estadoCivil = estadoCivilRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
        return toDTO(estadoCivil);
    }

    public EstadoCivilDTO create(EstadoCivilDTO dto) {
        String nombre = dto.getNombre().trim();
        if (estadoCivilRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        EstadoCivil estadoCivil = toEntity(dto);
        estadoCivil.setIdEstadoCivil(null);
        estadoCivil.setNombre(nombre);
        estadoCivil.setFechaCreacion(LocalDateTime.now());
        estadoCivil.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        EstadoCivil saved = estadoCivilRepository.save(estadoCivil);
        return toDTO(saved);
    }

    public EstadoCivilDTO update(Integer id, EstadoCivilDTO dto) {
        EstadoCivil existente = estadoCivilRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));

        String nombre = dto.getNombre().trim();
        if (estadoCivilRepository.existsByNombreExcluyendoId(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        EstadoCivil estadoCivil = toEntity(dto);
        estadoCivil.setIdEstadoCivil(id);
        estadoCivil.setNombre(nombre);
        estadoCivil.setFechaCreacion(existente.getFechaCreacion());
        estadoCivil.setUsuarioCreacion(existente.getUsuarioCreacion());
        estadoCivil.setFechaModificacion(LocalDateTime.now());
        estadoCivil.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        estadoCivilRepository.update(estadoCivil);
        return toDTO(estadoCivil);
    }

    /**
     * Si el estado civil esta asignado a personas, TRG_ESTADO_CIVIL_BAJA_VALIDA
     * (ORA-20102) rechaza el DELETE y GlobalExceptionHandler responde 409 con
     * el mensaje del trigger; aqui no se captura nada (D2).
     */
    public void delete(Integer id) {
        int filasAfectadas = estadoCivilRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw noEncontrado(id);
        }
    }

    private ResponseStatusException noEncontrado(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado civil no encontrado con id: " + id);
    }

    private EstadoCivilDTO toDTO(EstadoCivil estadoCivil) {
        EstadoCivilDTO dto = new EstadoCivilDTO();
        dto.setIdEstadoCivil(estadoCivil.getIdEstadoCivil());
        dto.setNombre(estadoCivil.getNombre());
        return dto;
    }

    private EstadoCivil toEntity(EstadoCivilDTO dto) {
        EstadoCivil estadoCivil = new EstadoCivil();
        estadoCivil.setIdEstadoCivil(dto.getIdEstadoCivil());
        estadoCivil.setNombre(dto.getNombre());
        return estadoCivil;
    }
}
