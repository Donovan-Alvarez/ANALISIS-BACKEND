package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.MenuOpcionesDTO;
import com.analisis_sistemas.erp.dto.ModuloMenuDTO;
import com.analisis_sistemas.erp.dto.OpcionPermisoDTO;
import com.analisis_sistemas.erp.entity.Usuario;
import com.analisis_sistemas.erp.repository.MenuUsuarioRepository;
import com.analisis_sistemas.erp.repository.MenuUsuarioRepository.FilaMenuRole;
import com.analisis_sistemas.erp.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuUsuarioService {

    private final MenuUsuarioRepository menuUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    public MenuUsuarioService(MenuUsuarioRepository menuUsuarioRepository, UsuarioRepository usuarioRepository) {
        this.menuUsuarioRepository = menuUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<ModuloMenuDTO> obtenerMenuDelUsuario(String idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        List<FilaMenuRole> filas = menuUsuarioRepository.findMenuPorRole(usuario.getIdRole());
        return agruparPorModuloYMenu(filas);
    }

    private List<ModuloMenuDTO> agruparPorModuloYMenu(List<FilaMenuRole> filas) {
        Map<Integer, ModuloMenuDTO> modulosPorId = new LinkedHashMap<>();
        Map<Integer, MenuOpcionesDTO> menusPorId = new LinkedHashMap<>();

        for (FilaMenuRole fila : filas) {
            ModuloMenuDTO modulo = modulosPorId.computeIfAbsent(fila.idModulo(), id -> {
                ModuloMenuDTO dto = new ModuloMenuDTO();
                dto.setIdModulo(fila.idModulo());
                dto.setNombre(fila.moduloNombre());
                dto.setOrdenMenu(fila.moduloOrden());
                dto.setMenus(new ArrayList<>());
                return dto;
            });

            MenuOpcionesDTO menu = menusPorId.computeIfAbsent(fila.idMenu(), id -> {
                MenuOpcionesDTO dto = new MenuOpcionesDTO();
                dto.setIdMenu(fila.idMenu());
                dto.setNombre(fila.menuNombre());
                dto.setOrdenMenu(fila.menuOrden());
                dto.setOpciones(new ArrayList<>());
                modulo.getMenus().add(dto);
                return dto;
            });

            OpcionPermisoDTO opcion = new OpcionPermisoDTO();
            opcion.setIdOpcion(fila.idOpcion());
            opcion.setNombre(fila.opcionNombre());
            opcion.setOrdenMenu(fila.opcionOrden());
            opcion.setPagina(fila.pagina());
            opcion.setAlta(fila.alta());
            opcion.setBaja(fila.baja());
            opcion.setCambio(fila.cambio());
            opcion.setImprimir(fila.imprimir());
            opcion.setExportar(fila.exportar());
            menu.getOpciones().add(opcion);
        }

        return new ArrayList<>(modulosPorId.values());
    }
}