package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.TipoDocumentoDTO;
import com.analisis_sistemas.erp.entity.TipoDocumento;
import com.analisis_sistemas.erp.repository.TipoDocumentoRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TipoDocumentoService {

    private static final String MENSAJE_DUPLICADO = "Ya existe un tipo de documento con ese nombre.";

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoService(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    public List<TipoDocumentoDTO> findAll() {
        return tipoDocumentoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public TipoDocumentoDTO findById(Integer id) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
        return toDTO(tipoDocumento);
    }

    public TipoDocumentoDTO create(TipoDocumentoDTO dto) {
        String nombre = dto.getNombre().trim();
        if (tipoDocumentoRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        TipoDocumento tipoDocumento = toEntity(dto);
        tipoDocumento.setIdTipoDocumento(null);
        tipoDocumento.setNombre(nombre);
        tipoDocumento.setFechaCreacion(LocalDateTime.now());
        tipoDocumento.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        TipoDocumento saved = tipoDocumentoRepository.save(tipoDocumento);
        return toDTO(saved);
    }

    public TipoDocumentoDTO update(Integer id, TipoDocumentoDTO dto) {
        TipoDocumento existente = tipoDocumentoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));

        String nombre = dto.getNombre().trim();
        if (tipoDocumentoRepository.existsByNombreExcluyendoId(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        TipoDocumento tipoDocumento = toEntity(dto);
        tipoDocumento.setIdTipoDocumento(id);
        tipoDocumento.setNombre(nombre);
        tipoDocumento.setFechaCreacion(existente.getFechaCreacion());
        tipoDocumento.setUsuarioCreacion(existente.getUsuarioCreacion());
        tipoDocumento.setFechaModificacion(LocalDateTime.now());
        tipoDocumento.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        tipoDocumentoRepository.update(tipoDocumento);
        return toDTO(tipoDocumento);
    }

    /**
     * Si el tipo de documento esta asignado a documentos de persona, TRG_TIPO_DOCUMENTO_BAJA_VALIDA
     * (ORA-20106) rechaza el DELETE y GlobalExceptionHandler responde 409 con
     * el mensaje del trigger; aqui no se captura nada (D2).
     */
    public void delete(Integer id) {
        int filasAfectadas = tipoDocumentoRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw noEncontrado(id);
        }
    }

    private ResponseStatusException noEncontrado(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de documento no encontrado con id: " + id);
    }

    private TipoDocumentoDTO toDTO(TipoDocumento tipoDocumento) {
        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.setIdTipoDocumento(tipoDocumento.getIdTipoDocumento());
        dto.setNombre(tipoDocumento.getNombre());
        return dto;
    }

    private TipoDocumento toEntity(TipoDocumentoDTO dto) {
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setIdTipoDocumento(dto.getIdTipoDocumento());
        tipoDocumento.setNombre(dto.getNombre());
        return tipoDocumento;
    }
}
