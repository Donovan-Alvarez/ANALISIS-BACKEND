package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.config.JwtProperties;
import com.analisis_sistemas.erp.dto.LoginRequestDTO;
import com.analisis_sistemas.erp.dto.LoginResponseDTO;
import com.analisis_sistemas.erp.entity.Empresa;
import com.analisis_sistemas.erp.entity.StatusUsuario;
import com.analisis_sistemas.erp.entity.Sucursal;
import com.analisis_sistemas.erp.entity.Usuario;
import com.analisis_sistemas.erp.repository.BitacoraAccesoRepository;
import com.analisis_sistemas.erp.repository.EmpresaRepository;
import com.analisis_sistemas.erp.repository.RoleRepository;
import com.analisis_sistemas.erp.repository.SesionRepository;
import com.analisis_sistemas.erp.repository.StatusUsuarioRepository;
import com.analisis_sistemas.erp.repository.SucursalRepository;
import com.analisis_sistemas.erp.repository.TipoAccesoRepository;
import com.analisis_sistemas.erp.repository.UsuarioRepository;
import com.analisis_sistemas.erp.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final String ACCESO_CONCEDIDO = "CONCEDIDO";
    private static final String ACCESO_DENEGADO = "DENEGADO";
    private static final String MENSAJE_ERROR_GENERICO = "Usuario o contrasena incorrectos";

    private final UsuarioRepository usuarioRepository;
    private final StatusUsuarioRepository statusUsuarioRepository;
    private final TipoAccesoRepository tipoAccesoRepository;
    private final BitacoraAccesoRepository bitacoraAccesoRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final RoleRepository roleRepository;
    private final SesionRepository sesionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(UsuarioRepository usuarioRepository,
                        StatusUsuarioRepository statusUsuarioRepository,
                        TipoAccesoRepository tipoAccesoRepository,
                        BitacoraAccesoRepository bitacoraAccesoRepository,
                        EmpresaRepository empresaRepository,
                        SucursalRepository sucursalRepository,
                        RoleRepository roleRepository,
                        SesionRepository sesionRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        JwtProperties jwtProperties) {
        this.usuarioRepository = usuarioRepository;
        this.statusUsuarioRepository = statusUsuarioRepository;
        this.tipoAccesoRepository = tipoAccesoRepository;
        this.bitacoraAccesoRepository = bitacoraAccesoRepository;
        this.empresaRepository = empresaRepository;
        this.sucursalRepository = sucursalRepository;
        this.roleRepository = roleRepository;
        this.sesionRepository = sesionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public LoginResponseDTO login(LoginRequestDTO dto, String httpUserAgent, String direccionIp) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getIdUsuario());

        if (usuarioOpt.isEmpty()) {
            Integer idTipoAcceso = obtenerIdTipoAcceso("Usuario ingresado no existe");
            bitacoraAccesoRepository.insertar(dto.getIdUsuario(), idTipoAcceso, httpUserAgent, direccionIp, ACCESO_DENEGADO, null);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MENSAJE_ERROR_GENERICO);
        }

        Usuario usuario = usuarioOpt.get();

        StatusUsuario statusActivo = obtenerStatusUsuarioPorNombre("Activo");
        if (!statusActivo.getIdStatusUsuario().equals(usuario.getIdStatusUsuario())) {
            Integer idTipoAcceso = obtenerIdTipoAcceso("Usuario Inactivo");
            bitacoraAccesoRepository.insertar(usuario.getIdUsuario(), idTipoAcceso, httpUserAgent, direccionIp, ACCESO_DENEGADO, null);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MENSAJE_ERROR_GENERICO);
        }

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            registrarIntentoFallido(usuario, httpUserAgent, direccionIp);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MENSAJE_ERROR_GENERICO);
        }

        String idSesion = UUID.randomUUID().toString();
        usuarioRepository.registrarLoginExitoso(usuario.getIdUsuario());
        sesionRepository.iniciarSesion(usuario.getIdUsuario(), idSesion, direccionIp, httpUserAgent);

        Integer idTipoAcceso = obtenerIdTipoAcceso("Acceso Concedido");
        bitacoraAccesoRepository.insertar(usuario.getIdUsuario(), idTipoAcceso, httpUserAgent, direccionIp, ACCESO_CONCEDIDO, idSesion);

        String nombreRole = roleRepository.findById(usuario.getIdRole())
                .map(role -> role.getNombre())
                .orElse(null);

        String token = jwtService.generateToken(usuario.getIdUsuario(), usuario.getIdRole(), usuario.getNombre(), nombreRole, idSesion);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setIdUsuario(usuario.getIdUsuario());
        response.setNombre(usuario.getNombre());
        response.setIdRole(usuario.getIdRole());
        response.setNombreRole(nombreRole);
        response.setExpiraEn(jwtProperties.getExpirationMs());

        return response;
    }

    public void logout(String idSesion) {
        sesionRepository.cerrarSesion(idSesion);
    }

    private void registrarIntentoFallido(Usuario usuario, String httpUserAgent, String direccionIp) {
        int intentosActuales = usuario.getIntentosDeAcceso() != null ? usuario.getIntentosDeAcceso() : 0;
        int nuevosIntentos = intentosActuales + 1;

        Sucursal sucursal = sucursalRepository.findById(usuario.getIdSucursal())
                .orElseThrow(() -> new IllegalStateException("La sucursal con id " + usuario.getIdSucursal() + " no existe"));
        Empresa empresa = empresaRepository.findById(sucursal.getIdEmpresa())
                .orElseThrow(() -> new IllegalStateException("La empresa con id " + sucursal.getIdEmpresa() + " no existe"));

        Integer limiteIntentos = empresa.getPasswordIntentosAntesDeBloquear();

        if (limiteIntentos != null && nuevosIntentos >= limiteIntentos) {
            StatusUsuario statusBloqueado = obtenerStatusUsuarioPorNombre("Bloqueado por intentos de acceso");
            usuarioRepository.registrarIntentoFallido(usuario.getIdUsuario(), nuevosIntentos, statusBloqueado.getIdStatusUsuario());

            Integer idTipoAcceso = obtenerIdTipoAcceso("Bloqueado - Numero de intentos exedidos");
            bitacoraAccesoRepository.insertar(usuario.getIdUsuario(), idTipoAcceso, httpUserAgent, direccionIp, ACCESO_DENEGADO, null);
        } else {
            usuarioRepository.registrarIntentoFallido(usuario.getIdUsuario(), nuevosIntentos, usuario.getIdStatusUsuario());

            Integer idTipoAcceso = obtenerIdTipoAcceso("Bloqueado - Password incorrecto");
            bitacoraAccesoRepository.insertar(usuario.getIdUsuario(), idTipoAcceso, httpUserAgent, direccionIp, ACCESO_DENEGADO, null);
        }
    }

    private Integer obtenerIdTipoAcceso(String nombre) {
        return tipoAccesoRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException("El tipo de acceso '" + nombre + "' no esta configurado en TIPO_ACCESO"))
                .getIdTipoAcceso();
    }

    private StatusUsuario obtenerStatusUsuarioPorNombre(String nombre) {
        return statusUsuarioRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException("El status de usuario '" + nombre + "' no esta configurado en STATUS_USUARIO"));
    }
}
