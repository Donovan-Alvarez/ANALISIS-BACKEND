package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.SucursalDTO;
import com.analisis_sistemas.erp.entity.Sucursal;
import com.analisis_sistemas.erp.repository.EmpresaRepository;
import com.analisis_sistemas.erp.repository.SucursalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;

    public SucursalService(SucursalRepository sucursalRepository, EmpresaRepository empresaRepository) {
        this.sucursalRepository = sucursalRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<SucursalDTO> findAll() {
        return sucursalRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public SucursalDTO findById(Integer id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada con id: " + id));
        return toDTO(sucursal);
    }

    public List<SucursalDTO> findByEmpresaId(Integer idEmpresa) {
        return sucursalRepository.findByEmpresaId(idEmpresa).stream()
                .map(this::toDTO)
                .toList();
    }

    public SucursalDTO create(SucursalDTO dto) {
        validarEmpresaExiste(dto.getIdEmpresa());

        Sucursal sucursal = toEntity(dto);
        sucursal.setFechaCreacion(LocalDateTime.now());
        // TODO: reemplazar por usuario autenticado via JWT
        sucursal.setUsuarioCreacion("system");

        Sucursal saved = sucursalRepository.save(sucursal);
        return toDTO(saved);
    }

    public SucursalDTO update(Integer id, SucursalDTO dto) {
        Sucursal existente = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada con id: " + id));

        validarEmpresaExiste(dto.getIdEmpresa());

        Sucursal sucursal = toEntity(dto);
        sucursal.setIdSucursal(id);
        sucursal.setFechaCreacion(existente.getFechaCreacion());
        sucursal.setUsuarioCreacion(existente.getUsuarioCreacion());
        sucursal.setFechaModificacion(LocalDateTime.now());
        // TODO: reemplazar por usuario autenticado via JWT
        sucursal.setUsuarioModificacion("system");

        sucursalRepository.update(sucursal);
        return toDTO(sucursal);
    }

    public void delete(Integer id) {
        int filasAfectadas = sucursalRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada con id: " + id);
        }
    }

    private void validarEmpresaExiste(Integer idEmpresa) {
        empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La empresa con id " + idEmpresa + " no existe"));
    }

    private SucursalDTO toDTO(Sucursal sucursal) {
        SucursalDTO dto = new SucursalDTO();
        dto.setIdSucursal(sucursal.getIdSucursal());
        dto.setNombre(sucursal.getNombre());
        dto.setDireccion(sucursal.getDireccion());
        dto.setIdEmpresa(sucursal.getIdEmpresa());
        return dto;
    }

    private Sucursal toEntity(SucursalDTO dto) {
        Sucursal sucursal = new Sucursal();
        sucursal.setIdSucursal(dto.getIdSucursal());
        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setIdEmpresa(dto.getIdEmpresa());
        return sucursal;
    }
}
