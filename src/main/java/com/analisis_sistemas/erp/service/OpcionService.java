package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.OpcionDTO;
import com.analisis_sistemas.erp.entity.Opcion;
import com.analisis_sistemas.erp.repository.MenuRepository;
import com.analisis_sistemas.erp.repository.OpcionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OpcionService {

    private final OpcionRepository opcionRepository;
    private final MenuRepository menuRepository;

    public OpcionService(OpcionRepository opcionRepository, MenuRepository menuRepository) {
        this.opcionRepository = opcionRepository;
        this.menuRepository = menuRepository;
    }

    public List<OpcionDTO> findAll() {
        return opcionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public OpcionDTO findById(Integer id) {
        Opcion opcion = opcionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opcion no encontrada con id: " + id));
        return toDTO(opcion);
    }

    public List<OpcionDTO> findByMenuId(Integer idMenu) {
        return opcionRepository.findByMenuId(idMenu).stream()
                .map(this::toDTO)
                .toList();
    }

    public OpcionDTO create(OpcionDTO dto) {
        validarMenuExiste(dto.getIdMenu());

        Opcion opcion = toEntity(dto);
        opcion.setFechaCreacion(LocalDateTime.now());
        // TODO: reemplazar por usuario autenticado via JWT
        opcion.setUsuarioCreacion("system");

        Opcion saved = opcionRepository.save(opcion);
        return toDTO(saved);
    }

    public OpcionDTO update(Integer id, OpcionDTO dto) {
        Opcion existente = opcionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opcion no encontrada con id: " + id));

        validarMenuExiste(dto.getIdMenu());

        Opcion opcion = toEntity(dto);
        opcion.setIdOpcion(id);
        opcion.setFechaCreacion(existente.getFechaCreacion());
        opcion.setUsuarioCreacion(existente.getUsuarioCreacion());
        opcion.setFechaModificacion(LocalDateTime.now());
        // TODO: reemplazar por usuario autenticado via JWT
        opcion.setUsuarioModificacion("system");

        opcionRepository.update(opcion);
        return toDTO(opcion);
    }

    public void delete(Integer id) {
        int filasAfectadas = opcionRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opcion no encontrada con id: " + id);
        }
    }

    private void validarMenuExiste(Integer idMenu) {
        menuRepository.findById(idMenu)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El menu con id " + idMenu + " no existe"));
    }

    private OpcionDTO toDTO(Opcion opcion) {
        OpcionDTO dto = new OpcionDTO();
        dto.setIdOpcion(opcion.getIdOpcion());
        dto.setIdMenu(opcion.getIdMenu());
        dto.setNombre(opcion.getNombre());
        dto.setOrdenMenu(opcion.getOrdenMenu());
        dto.setPagina(opcion.getPagina());
        return dto;
    }

    private Opcion toEntity(OpcionDTO dto) {
        Opcion opcion = new Opcion();
        opcion.setIdOpcion(dto.getIdOpcion());
        opcion.setIdMenu(dto.getIdMenu());
        opcion.setNombre(dto.getNombre());
        opcion.setOrdenMenu(dto.getOrdenMenu());
        opcion.setPagina(dto.getPagina());
        return opcion;
    }
}
