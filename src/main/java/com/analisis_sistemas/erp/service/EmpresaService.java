package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.EmpresaDTO;
import com.analisis_sistemas.erp.entity.Empresa;
import com.analisis_sistemas.erp.repository.EmpresaRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<EmpresaDTO> findAll() {
        return empresaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public EmpresaDTO findById(Integer id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada con id: " + id));
        return toDTO(empresa);
    }

    public EmpresaDTO create(EmpresaDTO dto) {
        Empresa empresa = toEntity(dto);
        empresa.setFechaCreacion(LocalDateTime.now());
        empresa.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Empresa saved = empresaRepository.save(empresa);
        return toDTO(saved);
    }

    public EmpresaDTO update(Integer id, EmpresaDTO dto) {
        Empresa existente = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada con id: " + id));

        Empresa empresa = toEntity(dto);
        empresa.setIdEmpresa(id);
        empresa.setFechaCreacion(existente.getFechaCreacion());
        empresa.setUsuarioCreacion(existente.getUsuarioCreacion());
        empresa.setFechaModificacion(LocalDateTime.now());
        empresa.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        empresaRepository.update(empresa);
        return toDTO(empresa);
    }

    public void delete(Integer id) {
        int filasAfectadas = empresaRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada con id: " + id);
        }
    }

    private EmpresaDTO toDTO(Empresa empresa) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setIdEmpresa(empresa.getIdEmpresa());
        dto.setNombre(empresa.getNombre());
        dto.setDireccion(empresa.getDireccion());
        dto.setNit(empresa.getNit());
        dto.setPasswordCantidadMayusculas(empresa.getPasswordCantidadMayusculas());
        dto.setPasswordCantidadMinusculas(empresa.getPasswordCantidadMinusculas());
        dto.setPasswordCantidadCaracteresEspeciales(empresa.getPasswordCantidadCaracteresEspeciales());
        dto.setPasswordCantidadCaducidadDias(empresa.getPasswordCantidadCaducidadDias());
        dto.setPasswordLargo(empresa.getPasswordLargo());
        dto.setPasswordIntentosAntesDeBloquear(empresa.getPasswordIntentosAntesDeBloquear());
        dto.setPasswordCantidadNumeros(empresa.getPasswordCantidadNumeros());
        dto.setPasswordCantidadPreguntasValidar(empresa.getPasswordCantidadPreguntasValidar());
        return dto;
    }

    private Empresa toEntity(EmpresaDTO dto) {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(dto.getIdEmpresa());
        empresa.setNombre(dto.getNombre());
        empresa.setDireccion(dto.getDireccion());
        empresa.setNit(dto.getNit());
        empresa.setPasswordCantidadMayusculas(dto.getPasswordCantidadMayusculas());
        empresa.setPasswordCantidadMinusculas(dto.getPasswordCantidadMinusculas());
        empresa.setPasswordCantidadCaracteresEspeciales(dto.getPasswordCantidadCaracteresEspeciales());
        empresa.setPasswordCantidadCaducidadDias(dto.getPasswordCantidadCaducidadDias());
        empresa.setPasswordLargo(dto.getPasswordLargo());
        empresa.setPasswordIntentosAntesDeBloquear(dto.getPasswordIntentosAntesDeBloquear());
        empresa.setPasswordCantidadNumeros(dto.getPasswordCantidadNumeros());
        empresa.setPasswordCantidadPreguntasValidar(dto.getPasswordCantidadPreguntasValidar());
        return empresa;
    }
}
