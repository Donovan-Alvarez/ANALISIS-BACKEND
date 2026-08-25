package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.CambiarPasswordRequestDTO;
import com.analisis_sistemas.erp.dto.PreguntaResponseDTO;
import com.analisis_sistemas.erp.dto.TokenResponseDTO;
import com.analisis_sistemas.erp.dto.ValidarRespuestaRequestDTO;
import com.analisis_sistemas.erp.entity.Empresa;
import com.analisis_sistemas.erp.entity.Usuario;
import com.analisis_sistemas.erp.repository.TokenRecuperacionRepository;
import com.analisis_sistemas.erp.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RecuperacionPasswordService {

    private static final String MENSAJE_USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final String MENSAJE_RESPUESTA_INCORRECTA = "Respuesta incorrecta";
    private static final String MENSAJE_TOKEN_INVALIDO = "Token invalido o expirado";

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final TokenRecuperacionRepository tokenRecuperacionRepository;
    private final PasswordEncoder passwordEncoder;

    public RecuperacionPasswordService(UsuarioRepository usuarioRepository,
                                        UsuarioService usuarioService,
                                        TokenRecuperacionRepository tokenRecuperacionRepository,
                                        PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.tokenRecuperacionRepository = tokenRecuperacionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PreguntaResponseDTO obtenerPregunta(String idUsuario) {
        Usuario usuario = buscarUsuario(idUsuario);

        PreguntaResponseDTO response = new PreguntaResponseDTO();
        response.setPregunta(usuario.getPregunta());
        return response;
    }

    public TokenResponseDTO validarRespuesta(ValidarRespuestaRequestDTO dto, String direccionIp) {
        Usuario usuario = buscarUsuario(dto.getIdUsuario());

        if (!passwordEncoder.matches(dto.getRespuesta(), usuario.getRespuesta())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MENSAJE_RESPUESTA_INCORRECTA);
        }

        String idToken = UUID.randomUUID().toString();
        tokenRecuperacionRepository.generarToken(usuario.getIdUsuario(), idToken, direccionIp);

        TokenResponseDTO response = new TokenResponseDTO();
        response.setIdToken(idToken);
        return response;
    }

    public void cambiarPassword(CambiarPasswordRequestDTO dto) {
        String idUsuario = tokenRecuperacionRepository.validarToken(dto.getIdToken());
        if (idUsuario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MENSAJE_TOKEN_INVALIDO);
        }

        Usuario usuario = buscarUsuario(idUsuario);
        Empresa empresa = usuarioService.obtenerEmpresaDesdeSucursal(usuario.getIdSucursal());

        usuarioService.validarPoliticaPassword(dto.getPasswordNuevo(), empresa);

        String passwordHash = passwordEncoder.encode(dto.getPasswordNuevo());
        LocalDateTime ultimaFechaCambioPassword = LocalDateTime.now();
        usuarioRepository.actualizarPassword(usuario.getIdUsuario(), passwordHash, ultimaFechaCambioPassword);

        tokenRecuperacionRepository.marcarTokenUtilizado(dto.getIdToken());
    }

    private Usuario buscarUsuario(String idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MENSAJE_USUARIO_NO_ENCONTRADO));
    }
}
