package com.analisis_sistemas.erp.controller;

import com.analisis_sistemas.erp.dto.CambiarPasswordRequestDTO;
import com.analisis_sistemas.erp.dto.PreguntaResponseDTO;
import com.analisis_sistemas.erp.dto.SolicitarPreguntaRequestDTO;
import com.analisis_sistemas.erp.dto.TokenResponseDTO;
import com.analisis_sistemas.erp.dto.ValidarRespuestaRequestDTO;
import com.analisis_sistemas.erp.service.RecuperacionPasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/recuperar-password")
public class RecuperacionPasswordController {

    private final RecuperacionPasswordService recuperacionPasswordService;

    public RecuperacionPasswordController(RecuperacionPasswordService recuperacionPasswordService) {
        this.recuperacionPasswordService = recuperacionPasswordService;
    }

    @PostMapping("/pregunta")
    public ResponseEntity<PreguntaResponseDTO> obtenerPregunta(@Valid @RequestBody SolicitarPreguntaRequestDTO dto) {
        return ResponseEntity.ok(recuperacionPasswordService.obtenerPregunta(dto.getIdUsuario()));
    }

    @PostMapping("/validar-respuesta")
    public ResponseEntity<TokenResponseDTO> validarRespuesta(@Valid @RequestBody ValidarRespuestaRequestDTO dto, HttpServletRequest request) {
        String direccionIp = request.getRemoteAddr();
        return ResponseEntity.ok(recuperacionPasswordService.validarRespuesta(dto, direccionIp));
    }

    @PostMapping("/cambiar")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambiarPasswordRequestDTO dto) {
        recuperacionPasswordService.cambiarPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
