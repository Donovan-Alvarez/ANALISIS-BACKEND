package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.DocumentoPersonaDTO;
import com.analisis_sistemas.erp.service.DocumentoPersonaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * El primer segmento (/api/documentos-persona) debe ser igual a OPCION.Pagina = 'documentos-persona'
 * (contrato-rutas.md). La llave es compuesta: /{idTipoDocumento}/{idPersona}.
 */
@RestController
@RequestMapping("/api/documentos-persona")
public class DocumentoPersonaController {

    private final DocumentoPersonaService documentoPersonaService;

    public DocumentoPersonaController(DocumentoPersonaService documentoPersonaService) {
        this.documentoPersonaService = documentoPersonaService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentoPersonaDTO>> findAll() {
        return ResponseEntity.ok(documentoPersonaService.findAll());
    }

    @GetMapping("/por-persona/{idPersona}")
    public ResponseEntity<List<DocumentoPersonaDTO>> findByPersonaId(@PathVariable Integer idPersona) {
        return ResponseEntity.ok(documentoPersonaService.findByPersonaId(idPersona));
    }

    @GetMapping("/{idTipoDocumento}/{idPersona}")
    public ResponseEntity<DocumentoPersonaDTO> findById(@PathVariable Integer idTipoDocumento,
                                                        @PathVariable Integer idPersona) {
        return ResponseEntity.ok(documentoPersonaService.findById(idTipoDocumento, idPersona));
    }

    @PostMapping
    public ResponseEntity<DocumentoPersonaDTO> create(@Valid @RequestBody DocumentoPersonaDTO dto) {
        DocumentoPersonaDTO creado = documentoPersonaService.create(dto);
        return ResponseEntity.created(URI.create(
                "/api/documentos-persona/" + creado.getIdTipoDocumento() + "/" + creado.getIdPersona())).body(creado);
    }

    /** Solo cambia el número de documento; tipo y persona de la URL son la llave. */
    @PutMapping("/{idTipoDocumento}/{idPersona}")
    public ResponseEntity<DocumentoPersonaDTO> update(@PathVariable Integer idTipoDocumento,
                                                      @PathVariable Integer idPersona,
                                                      @Valid @RequestBody DocumentoPersonaDTO dto) {
        return ResponseEntity.ok(documentoPersonaService.update(idTipoDocumento, idPersona, dto));
    }

    @DeleteMapping("/{idTipoDocumento}/{idPersona}")
    public ResponseEntity<Void> delete(@PathVariable Integer idTipoDocumento, @PathVariable Integer idPersona) {
        documentoPersonaService.delete(idTipoDocumento, idPersona);
        return ResponseEntity.noContent().build();
    }
}
