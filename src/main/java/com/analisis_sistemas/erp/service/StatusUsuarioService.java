package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.StatusUsuarioDTO;
import com.analisis_sistemas.erp.entity.StatusUsuario;
import com.analisis_sistemas.erp.repository.StatusUsuarioRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusUsuarioService {

    private final StatusUsuarioRepository statusUsuarioRepository;

    public StatusUsuarioService(StatusUsuarioRepository statusUsuarioRepository) {
        this.statusUsuarioRepository = statusUsuarioRepository;
    }

    public List<StatusUsuarioDTO> findAll() {
        return statusUsuarioRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public StatusUsuarioDTO findById(Integer id) {
        StatusUsuario statusUsuario = statusUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status de usuario no encontrado con id: " + id));
        return toDTO(statusUsuario);
    }

    public StatusUsuarioDTO create(StatusUsuarioDTO dto) {
        StatusUsuario statusUsuario = toEntity(dto);
        statusUsuario.setFechaCreacion(LocalDateTime.now());
        statusUsuario.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        StatusUsuario saved = statusUsuarioRepository.save(statusUsuario);
        return toDTO(saved);
    }

    public StatusUsuarioDTO update(Integer id, StatusUsuarioDTO dto) {
        StatusUsuario existente = statusUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status de usuario no encontrado con id: " + id));

        StatusUsuario statusUsuario = toEntity(dto);
        statusUsuario.setIdStatusUsuario(id);
        statusUsuario.setFechaCreacion(existente.getFechaCreacion());
        statusUsuario.setUsuarioCreacion(existente.getUsuarioCreacion());
        statusUsuario.setFechaModificacion(LocalDateTime.now());
        statusUsuario.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        statusUsuarioRepository.update(statusUsuario);
        return toDTO(statusUsuario);
    }

    public void delete(Integer id) {
        int filasAfectadas = statusUsuarioRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Status de usuario no encontrado con id: " + id);
        }
    }

    private StatusUsuarioDTO toDTO(StatusUsuario statusUsuario) {
        StatusUsuarioDTO dto = new StatusUsuarioDTO();
        dto.setIdStatusUsuario(statusUsuario.getIdStatusUsuario());
        dto.setNombre(statusUsuario.getNombre());
        return dto;
    }

    private StatusUsuario toEntity(StatusUsuarioDTO dto) {
        StatusUsuario statusUsuario = new StatusUsuario();
        statusUsuario.setIdStatusUsuario(dto.getIdStatusUsuario());
        statusUsuario.setNombre(dto.getNombre());
        return statusUsuario;
    }
}
