package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.ModuloDTO;
import com.analisis_sistemas.erp.entity.Modulo;
import com.analisis_sistemas.erp.repository.ModuloRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModuloService {

    private final ModuloRepository moduloRepository;

    public ModuloService(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    public List<ModuloDTO> findAll() {
        return moduloRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ModuloDTO findById(Integer id) {
        Modulo modulo = moduloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Modulo no encontrado con id: " + id));
        return toDTO(modulo);
    }

    public ModuloDTO create(ModuloDTO dto) {
        Modulo modulo = toEntity(dto);
        modulo.setFechaCreacion(LocalDateTime.now());
        modulo.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Modulo saved = moduloRepository.save(modulo);
        return toDTO(saved);
    }

    public ModuloDTO update(Integer id, ModuloDTO dto) {
        Modulo existente = moduloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Modulo no encontrado con id: " + id));

        Modulo modulo = toEntity(dto);
        modulo.setIdModulo(id);
        modulo.setFechaCreacion(existente.getFechaCreacion());
        modulo.setUsuarioCreacion(existente.getUsuarioCreacion());
        modulo.setFechaModificacion(LocalDateTime.now());
        modulo.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        moduloRepository.update(modulo);
        return toDTO(modulo);
    }

    public void delete(Integer id) {
        int filasAfectadas = moduloRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Modulo no encontrado con id: " + id);
        }
    }

    private ModuloDTO toDTO(Modulo modulo) {
        ModuloDTO dto = new ModuloDTO();
        dto.setIdModulo(modulo.getIdModulo());
        dto.setNombre(modulo.getNombre());
        dto.setOrdenMenu(modulo.getOrdenMenu());
        return dto;
    }

    private Modulo toEntity(ModuloDTO dto) {
        Modulo modulo = new Modulo();
        modulo.setIdModulo(dto.getIdModulo());
        modulo.setNombre(dto.getNombre());
        modulo.setOrdenMenu(dto.getOrdenMenu());
        return modulo;
    }
}
