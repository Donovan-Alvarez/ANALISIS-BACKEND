package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.UsuarioRequestDTO;
import com.analisis_sistemas.erp.dto.UsuarioResponseDTO;
import com.analisis_sistemas.erp.entity.Empresa;
import com.analisis_sistemas.erp.entity.Sucursal;
import com.analisis_sistemas.erp.entity.Usuario;
import com.analisis_sistemas.erp.repository.EmpresaRepository;
import com.analisis_sistemas.erp.repository.GeneroRepository;
import com.analisis_sistemas.erp.repository.RoleRepository;
import com.analisis_sistemas.erp.repository.StatusUsuarioRepository;
import com.analisis_sistemas.erp.repository.SucursalRepository;
import com.analisis_sistemas.erp.repository.UsuarioRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final StatusUsuarioRepository statusUsuarioRepository;
    private final GeneroRepository generoRepository;
    private final RoleRepository roleRepository;
    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                           StatusUsuarioRepository statusUsuarioRepository,
                           GeneroRepository generoRepository,
                           RoleRepository roleRepository,
                           SucursalRepository sucursalRepository,
                           EmpresaRepository empresaRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.statusUsuarioRepository = statusUsuarioRepository;
        this.generoRepository = generoRepository;
        this.roleRepository = roleRepository;
        this.sucursalRepository = sucursalRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UsuarioResponseDTO findById(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
        return toResponseDTO(usuario);
    }

    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El password es obligatorio al crear un usuario");
        }

        if (usuarioRepository.findById(dto.getIdUsuario()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario " + dto.getIdUsuario() + " ya existe");
        }

        validarStatusUsuarioExiste(dto.getIdStatusUsuario());
        validarGeneroExiste(dto.getIdGenero());
        validarRoleExiste(dto.getIdRole());
        Empresa empresa = obtenerEmpresaDesdeSucursal(dto.getIdSucursal());

        validarPoliticaPassword(dto.getPassword(), empresa);

        Usuario usuario = toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRespuesta(passwordEncoder.encode(dto.getRespuesta()));
        usuario.setIntentosDeAcceso(0);
        usuario.setUltimaFechaIngreso(null);
        usuario.setSesionActual(null);
        usuario.setUltimaFechaCambioPassword(LocalDateTime.now());
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Usuario saved = usuarioRepository.save(usuario);
        return toResponseDTO(saved);
    }

    public UsuarioResponseDTO update(String id, UsuarioRequestDTO dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));

        validarStatusUsuarioExiste(dto.getIdStatusUsuario());
        validarGeneroExiste(dto.getIdGenero());
        validarRoleExiste(dto.getIdRole());
        Empresa empresa = obtenerEmpresaDesdeSucursal(dto.getIdSucursal());

        Usuario usuario = toEntity(dto);
        usuario.setIdUsuario(id);

        boolean cambiaPassword = dto.getPassword() != null && !dto.getPassword().isBlank();
        if (cambiaPassword) {
            validarPoliticaPassword(dto.getPassword(), empresa);
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            usuario.setUltimaFechaCambioPassword(LocalDateTime.now());
        } else {
            usuario.setPassword(existente.getPassword());
            usuario.setUltimaFechaCambioPassword(existente.getUltimaFechaCambioPassword());
        }

        boolean cambiaRespuesta = dto.getRespuesta() != null && !dto.getRespuesta().isBlank();
        if (cambiaRespuesta) {
            usuario.setRespuesta(passwordEncoder.encode(dto.getRespuesta()));
        } else {
            usuario.setRespuesta(existente.getRespuesta());
        }

        usuario.setIntentosDeAcceso(existente.getIntentosDeAcceso());
        usuario.setUltimaFechaIngreso(existente.getUltimaFechaIngreso());
        usuario.setSesionActual(existente.getSesionActual());
        usuario.setFechaCreacion(existente.getFechaCreacion());
        usuario.setUsuarioCreacion(existente.getUsuarioCreacion());
        usuario.setFechaModificacion(LocalDateTime.now());
        usuario.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        usuarioRepository.update(usuario);
        return toResponseDTO(usuario);
    }

    public void delete(String id) {
        int filasAfectadas = usuarioRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
        }
    }

    private void validarStatusUsuarioExiste(Integer idStatusUsuario) {
        statusUsuarioRepository.findById(idStatusUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El status de usuario con id " + idStatusUsuario + " no existe"));
    }

    private void validarGeneroExiste(Integer idGenero) {
        generoRepository.findById(idGenero)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El genero con id " + idGenero + " no existe"));
    }

    private void validarRoleExiste(Integer idRole) {
        roleRepository.findById(idRole)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El role con id " + idRole + " no existe"));
    }

    private Empresa obtenerEmpresaDesdeSucursal(Integer idSucursal) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sucursal con id " + idSucursal + " no existe"));

        return empresaRepository.findById(sucursal.getIdEmpresa())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La empresa con id " + sucursal.getIdEmpresa() + " no existe"));
    }

    void validarPoliticaPassword(String password, Empresa empresa) {
        Integer largoMinimo = empresa.getPasswordLargo();
        if (largoMinimo != null && password.length() < largoMinimo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contrasena debe tener al menos " + largoMinimo + " caracteres");
        }

        Integer minMayusculas = empresa.getPasswordCantidadMayusculas();
        if (minMayusculas != null) {
            long mayusculas = password.chars().filter(Character::isUpperCase).count();
            if (mayusculas < minMayusculas) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La contrasena debe tener al menos " + minMayusculas + " letras mayusculas");
            }
        }

        Integer minMinusculas = empresa.getPasswordCantidadMinusculas();
        if (minMinusculas != null) {
            long minusculas = password.chars().filter(Character::isLowerCase).count();
            if (minusculas < minMinusculas) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La contrasena debe tener al menos " + minMinusculas + " letras minusculas");
            }
        }

        Integer minNumeros = empresa.getPasswordCantidadNumeros();
        if (minNumeros != null) {
            long numeros = password.chars().filter(Character::isDigit).count();
            if (numeros < minNumeros) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La contrasena debe tener al menos " + minNumeros + " numeros");
            }
        }

        Integer minEspeciales = empresa.getPasswordCantidadCaracteresEspeciales();
        if (minEspeciales != null) {
            long especiales = password.chars()
                    .filter(c -> !Character.isLetterOrDigit(c))
                    .count();
            if (especiales < minEspeciales) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La contrasena debe tener al menos " + minEspeciales + " caracteres especiales");
            }
        }
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setIdStatusUsuario(usuario.getIdStatusUsuario());
        dto.setIdGenero(usuario.getIdGenero());
        dto.setUltimaFechaIngreso(usuario.getUltimaFechaIngreso());
        dto.setIntentosDeAcceso(usuario.getIntentosDeAcceso());
        dto.setSesionActual(usuario.getSesionActual());
        dto.setUltimaFechaCambioPassword(usuario.getUltimaFechaCambioPassword());
        dto.setCorreoElectronico(usuario.getCorreoElectronico());
        dto.setRequiereCambiarPassword(usuario.getRequiereCambiarPassword());
        dto.setFotografiaBase64(usuario.getFotografia() != null
                ? Base64.getEncoder().encodeToString(usuario.getFotografia())
                : null);
        dto.setTelefonoMovil(usuario.getTelefonoMovil());
        dto.setIdSucursal(usuario.getIdSucursal());
        dto.setPregunta(usuario.getPregunta());
        dto.setRespuesta(usuario.getRespuesta());
        dto.setIdRole(usuario.getIdRole());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setUsuarioCreacion(usuario.getUsuarioCreacion());
        dto.setFechaModificacion(usuario.getFechaModificacion());
        dto.setUsuarioModificacion(usuario.getUsuarioModificacion());
        return dto;
    }

    private Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(dto.getIdUsuario());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setIdStatusUsuario(dto.getIdStatusUsuario());
        usuario.setIdGenero(dto.getIdGenero());
        usuario.setCorreoElectronico(dto.getCorreoElectronico());
        usuario.setRequiereCambiarPassword(dto.getRequiereCambiarPassword());
        usuario.setFotografia(dto.getFotografiaBase64() != null
                ? Base64.getDecoder().decode(dto.getFotografiaBase64())
                : null);
        usuario.setTelefonoMovil(dto.getTelefonoMovil());
        usuario.setIdSucursal(dto.getIdSucursal());
        usuario.setPregunta(dto.getPregunta());
        usuario.setIdRole(dto.getIdRole());
        return usuario;
    }
}
