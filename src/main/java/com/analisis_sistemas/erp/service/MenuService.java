package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.MenuDTO;
import com.analisis_sistemas.erp.entity.Menu;
import com.analisis_sistemas.erp.repository.MenuRepository;
import com.analisis_sistemas.erp.repository.ModuloRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final ModuloRepository moduloRepository;

    public MenuService(MenuRepository menuRepository, ModuloRepository moduloRepository) {
        this.menuRepository = menuRepository;
        this.moduloRepository = moduloRepository;
    }

    public List<MenuDTO> findAll() {
        return menuRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public MenuDTO findById(Integer id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu no encontrado con id: " + id));
        return toDTO(menu);
    }

    public List<MenuDTO> findByModuloId(Integer idModulo) {
        return menuRepository.findByModuloId(idModulo).stream()
                .map(this::toDTO)
                .toList();
    }

    public MenuDTO create(MenuDTO dto) {
        validarModuloExiste(dto.getIdModulo());

        Menu menu = toEntity(dto);
        menu.setFechaCreacion(LocalDateTime.now());
        menu.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Menu saved = menuRepository.save(menu);
        return toDTO(saved);
    }

    public MenuDTO update(Integer id, MenuDTO dto) {
        Menu existente = menuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu no encontrado con id: " + id));

        validarModuloExiste(dto.getIdModulo());

        Menu menu = toEntity(dto);
        menu.setIdMenu(id);
        menu.setFechaCreacion(existente.getFechaCreacion());
        menu.setUsuarioCreacion(existente.getUsuarioCreacion());
        menu.setFechaModificacion(LocalDateTime.now());
        menu.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        menuRepository.update(menu);
        return toDTO(menu);
    }

    public void delete(Integer id) {
        int filasAfectadas = menuRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu no encontrado con id: " + id);
        }
    }

    private void validarModuloExiste(Integer idModulo) {
        moduloRepository.findById(idModulo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El modulo con id " + idModulo + " no existe"));
    }

    private MenuDTO toDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setIdMenu(menu.getIdMenu());
        dto.setIdModulo(menu.getIdModulo());
        dto.setNombre(menu.getNombre());
        dto.setOrdenMenu(menu.getOrdenMenu());
        return dto;
    }

    private Menu toEntity(MenuDTO dto) {
        Menu menu = new Menu();
        menu.setIdMenu(dto.getIdMenu());
        menu.setIdModulo(dto.getIdModulo());
        menu.setNombre(dto.getNombre());
        menu.setOrdenMenu(dto.getOrdenMenu());
        return menu;
    }
}
