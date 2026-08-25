package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.MenuAsignacionDTO;
import com.analisis_sistemas.erp.dto.OpcionAsignacionDTO;
import com.analisis_sistemas.erp.dto.OpcionPermisoRequestDTO;
import com.analisis_sistemas.erp.dto.RoleOpcionesResponseDTO;
import com.analisis_sistemas.erp.repository.ModuloRepository;
import com.analisis_sistemas.erp.repository.RoleOpcionRepository;
import com.analisis_sistemas.erp.repository.RoleOpcionRepository.FilaOpcionRole;
import com.analisis_sistemas.erp.repository.RoleRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RoleOpcionService {

    private final RoleOpcionRepository roleOpcionRepository;
    private final RoleRepository roleRepository;
    private final ModuloRepository moduloRepository;

    public RoleOpcionService(RoleOpcionRepository roleOpcionRepository, RoleRepository roleRepository,
                              ModuloRepository moduloRepository) {
        this.roleOpcionRepository = roleOpcionRepository;
        this.roleRepository = roleRepository;
        this.moduloRepository = moduloRepository;
    }

    public RoleOpcionesResponseDTO obtenerPorModulo(Integer idRole, Integer idModulo) {
        validarRoleExiste(idRole);
        validarModuloExiste(idModulo);

        List<FilaOpcionRole> filas = roleOpcionRepository.findOpcionesPorRoleYModulo(idRole, idModulo);
        return armarRespuesta(idRole, idModulo, filas);
    }

    @Transactional
    public RoleOpcionesResponseDTO guardarPorModulo(Integer idRole, Integer idModulo, List<OpcionPermisoRequestDTO> permisos) {
        validarRoleExiste(idRole);
        validarModuloExiste(idModulo);

        List<Integer> idsOpcionesDelModulo = roleOpcionRepository.findIdsOpcionesPorModulo(idModulo);
        Set<Integer> idsValidos = Set.copyOf(idsOpcionesDelModulo);

        for (OpcionPermisoRequestDTO permiso : permisos) {
            if (!idsValidos.contains(permiso.getIdOpcion())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La opcion con id " + permiso.getIdOpcion() + " no pertenece al modulo " + idModulo);
            }
        }

        roleOpcionRepository.deletePermisos(idRole, idsOpcionesDelModulo);

        String usuarioAutenticado = SecurityUtils.getUsuarioAutenticado();
        LocalDateTime fechaCreacion = LocalDateTime.now();
        for (OpcionPermisoRequestDTO permiso : permisos) {
            // Tolerancia: si vino algun flag de accion en true aunque "consultar" haya llegado
            // en false, igual se guarda la fila con los flags reales (sin forzarlos a partir de consultar).
            boolean seGuarda = permiso.isConsultar() || permiso.isAlta() || permiso.isBaja()
                    || permiso.isCambio() || permiso.isImprimir() || permiso.isExportar();

            if (seGuarda) {
                roleOpcionRepository.insertPermiso(idRole, permiso.getIdOpcion(),
                        permiso.isAlta(), permiso.isBaja(), permiso.isCambio(),
                        permiso.isImprimir(), permiso.isExportar(), usuarioAutenticado, fechaCreacion);
            }
        }

        return obtenerPorModulo(idRole, idModulo);
    }

    private void validarRoleExiste(Integer idRole) {
        roleRepository.findById(idRole)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role no encontrado con id: " + idRole));
    }

    private void validarModuloExiste(Integer idModulo) {
        moduloRepository.findById(idModulo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Modulo no encontrado con id: " + idModulo));
    }

    private RoleOpcionesResponseDTO armarRespuesta(Integer idRole, Integer idModulo, List<FilaOpcionRole> filas) {
        Map<Integer, MenuAsignacionDTO> menusPorId = new LinkedHashMap<>();

        for (FilaOpcionRole fila : filas) {
            MenuAsignacionDTO menu = menusPorId.computeIfAbsent(fila.idMenu(), id -> {
                MenuAsignacionDTO dto = new MenuAsignacionDTO();
                dto.setIdMenu(fila.idMenu());
                dto.setNombre(fila.menuNombre());
                dto.setOpciones(new ArrayList<>());
                return dto;
            });

            OpcionAsignacionDTO opcion = new OpcionAsignacionDTO();
            opcion.setIdOpcion(fila.idOpcion());
            opcion.setNombre(fila.opcionNombre());
            opcion.setConsultar(fila.consultar());
            opcion.setAlta(fila.alta());
            opcion.setBaja(fila.baja());
            opcion.setCambio(fila.cambio());
            opcion.setImprimir(fila.imprimir());
            opcion.setExportar(fila.exportar());
            menu.getOpciones().add(opcion);
        }

        RoleOpcionesResponseDTO response = new RoleOpcionesResponseDTO();
        response.setIdRole(idRole);
        response.setIdModulo(idModulo);
        response.setMenus(new ArrayList<>(menusPorId.values()));
        return response;
    }
}
