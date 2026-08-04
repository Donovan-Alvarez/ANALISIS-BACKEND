package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.GeneroDTO;
import com.analisis_sistemas.erp.entity.Genero;
import com.analisis_sistemas.erp.repository.GeneroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeneroService {

    private final GeneroRepository generoRepository;

    public GeneroService(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    public List<GeneroDTO> findAll() {
        return generoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public GeneroDTO findById(Integer id) {
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genero no encontrado con id: " + id));
        return toDTO(genero);
    }

    public GeneroDTO create(GeneroDTO dto) {
        Genero genero = toEntity(dto);
        genero.setFechaCreacion(LocalDateTime.now());
        // TODO: reemplazar por usuario autenticado via JWT
        genero.setUsuarioCreacion("system");

        Genero saved = generoRepository.save(genero);
        return toDTO(saved);
    }

    public GeneroDTO update(Integer id, GeneroDTO dto) {
        Genero existente = generoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genero no encontrado con id: " + id));

        Genero genero = toEntity(dto);
        genero.setIdGenero(id);
        genero.setFechaCreacion(existente.getFechaCreacion());
        genero.setUsuarioCreacion(existente.getUsuarioCreacion());
        genero.setFechaModificacion(LocalDateTime.now());
        // TODO: reemplazar por usuario autenticado via JWT
        genero.setUsuarioModificacion("system");

        generoRepository.update(genero);
        return toDTO(genero);
    }

    public void delete(Integer id) {
        int filasAfectadas = generoRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genero no encontrado con id: " + id);
        }
    }

    private GeneroDTO toDTO(Genero genero) {
        GeneroDTO dto = new GeneroDTO();
        dto.setIdGenero(genero.getIdGenero());
        dto.setNombre(genero.getNombre());
        return dto;
    }

    private Genero toEntity(GeneroDTO dto) {
        Genero genero = new Genero();
        genero.setIdGenero(dto.getIdGenero());
        genero.setNombre(dto.getNombre());
        return genero;
    }
}
