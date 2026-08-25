package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.RoleDTO;
import com.analisis_sistemas.erp.entity.Role;
import com.analisis_sistemas.erp.repository.RoleRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> findAll() {
        return roleRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public RoleDTO findById(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role no encontrado con id: " + id));
        return toDTO(role);
    }

    public RoleDTO create(RoleDTO dto) {
        Role role = toEntity(dto);
        role.setFechaCreacion(LocalDateTime.now());
        role.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Role saved = roleRepository.save(role);
        return toDTO(saved);
    }

    public RoleDTO update(Integer id, RoleDTO dto) {
        Role existente = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role no encontrado con id: " + id));

        Role role = toEntity(dto);
        role.setIdRole(id);
        role.setFechaCreacion(existente.getFechaCreacion());
        role.setUsuarioCreacion(existente.getUsuarioCreacion());
        role.setFechaModificacion(LocalDateTime.now());
        role.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        roleRepository.update(role);
        return toDTO(role);
    }

    public void delete(Integer id) {
        int filasAfectadas = roleRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role no encontrado con id: " + id);
        }
    }

    private RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setIdRole(role.getIdRole());
        dto.setNombre(role.getNombre());
        return dto;
    }

    private Role toEntity(RoleDTO dto) {
        Role role = new Role();
        role.setIdRole(dto.getIdRole());
        role.setNombre(dto.getNombre());
        return role;
    }
}
