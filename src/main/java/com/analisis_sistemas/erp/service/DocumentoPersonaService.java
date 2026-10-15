package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.DocumentoPersonaDTO;
import com.analisis_sistemas.erp.entity.DocumentoPersona;
import com.analisis_sistemas.erp.entity.TipoDocumento;
import com.analisis_sistemas.erp.repository.DocumentoPersonaRepository;
import com.analisis_sistemas.erp.repository.PersonaRepository;
import com.analisis_sistemas.erp.repository.TipoDocumentoRepository;
import com.analisis_sistemas.erp.utils.NumeroDocumentoValidador;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentoPersonaService {

    private final DocumentoPersonaRepository documentoPersonaRepository;
    private final PersonaRepository personaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public DocumentoPersonaService(DocumentoPersonaRepository documentoPersonaRepository,
                                   PersonaRepository personaRepository,
                                   TipoDocumentoRepository tipoDocumentoRepository) {
        this.documentoPersonaRepository = documentoPersonaRepository;
        this.personaRepository = personaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    public List<DocumentoPersonaDTO> findAll() {
        return documentoPersonaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<DocumentoPersonaDTO> findByPersonaId(Integer idPersona) {
        return documentoPersonaRepository.findByPersonaId(idPersona).stream()
                .map(this::toDTO)
                .toList();
    }

    public DocumentoPersonaDTO findById(Integer idTipoDocumento, Integer idPersona) {
        DocumentoPersona documento = documentoPersonaRepository.findById(idTipoDocumento, idPersona)
                .orElseThrow(() -> noEncontrado(idTipoDocumento, idPersona));
        return toDTO(documento);
    }

    public DocumentoPersonaDTO create(DocumentoPersonaDTO dto) {
        personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> badRequest("La persona seleccionada no existe."));
        TipoDocumento tipo = buscarTipo(dto.getIdTipoDocumento());

        String numero = validarNumero(tipo, dto.getNoDocumento());


        if (documentoPersonaRepository.existsByPersonaYTipo(dto.getIdPersona(), dto.getIdTipoDocumento())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esta persona ya tiene registrado un documento de tipo \"" + tipo.getNombre() + "\".");
        }

        DocumentoPersona documento = new DocumentoPersona();
        documento.setIdTipoDocumento(dto.getIdTipoDocumento());
        documento.setIdPersona(dto.getIdPersona());
        documento.setNoDocumento(numero);
        documento.setFechaCreacion(LocalDateTime.now());
        documento.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        documentoPersonaRepository.save(documento);
        return findById(dto.getIdTipoDocumento(), dto.getIdPersona());
    }

   
    public DocumentoPersonaDTO update(Integer idTipoDocumento, Integer idPersona, DocumentoPersonaDTO dto) {
        DocumentoPersona existente = documentoPersonaRepository.findById(idTipoDocumento, idPersona)
                .orElseThrow(() -> noEncontrado(idTipoDocumento, idPersona));

        TipoDocumento tipo = buscarTipo(idTipoDocumento);
        String numero = validarNumero(tipo, dto.getNoDocumento());

        DocumentoPersona documento = new DocumentoPersona();
        documento.setIdTipoDocumento(idTipoDocumento);
        documento.setIdPersona(idPersona);
        documento.setNoDocumento(numero);
        documento.setFechaCreacion(existente.getFechaCreacion());
        documento.setUsuarioCreacion(existente.getUsuarioCreacion());
        documento.setFechaModificacion(LocalDateTime.now());
        documento.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        documentoPersonaRepository.update(documento);
        return findById(idTipoDocumento, idPersona);
    }

    public void delete(Integer idTipoDocumento, Integer idPersona) {
        int filasAfectadas = documentoPersonaRepository.deleteById(idTipoDocumento, idPersona);
        if (filasAfectadas == 0) {
            throw noEncontrado(idTipoDocumento, idPersona);
        }
    }


    private TipoDocumento buscarTipo(Integer idTipoDocumento) {
        return tipoDocumentoRepository.findById(idTipoDocumento)
                .orElseThrow(() -> badRequest("El tipo de documento seleccionado no existe."));
    }

    private String validarNumero(TipoDocumento tipo, String numero) {
        try {
            return NumeroDocumentoValidador.validarYNormalizar(tipo.getNombre(), numero);
        } catch (IllegalArgumentException ex) {
            throw badRequest(ex.getMessage());
        }
    }

    private DocumentoPersonaDTO toDTO(DocumentoPersona documento) {
        DocumentoPersonaDTO dto = new DocumentoPersonaDTO();
        dto.setIdTipoDocumento(documento.getIdTipoDocumento());
        dto.setIdPersona(documento.getIdPersona());
        dto.setNoDocumento(documento.getNoDocumento());
        dto.setNombreTipoDocumento(documento.getNombreTipoDocumento());
        dto.setNombrePersona(documento.getNombrePersona());
        return dto;
    }

    private static ResponseStatusException badRequest(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private ResponseStatusException noEncontrado(Integer idTipoDocumento, Integer idPersona) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Documento no encontrado (tipo " + idTipoDocumento + ", persona " + idPersona + ").");
    }
}
