package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.BancoDTO;
import com.analisis_sistemas.erp.entity.Banco;
import com.analisis_sistemas.erp.repository.BancoRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BancoService {

    private static final String MENSAJE_DUPLICADO = "Ya existe un banco con ese nombre.";

    private final BancoRepository bancoRepository;

    public BancoService(BancoRepository bancoRepository) {
        this.bancoRepository = bancoRepository;
    }

    public List<BancoDTO> findAll() {
        return bancoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public BancoDTO findById(Integer id) {
        Banco banco = bancoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
        return toDTO(banco);
    }

    public BancoDTO create(BancoDTO dto) {
        String nombre = dto.getNombre().trim();
        if (bancoRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        Banco banco = toEntity(dto);
        banco.setIdBanco(null);
        banco.setNombre(nombre);
        banco.setFechaCreacion(LocalDateTime.now());
        banco.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Banco saved = bancoRepository.save(banco);
        return toDTO(saved);
    }

    public BancoDTO update(Integer id, BancoDTO dto) {
        Banco existente = bancoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));

        String nombre = dto.getNombre().trim();
        if (bancoRepository.existsByNombreExcluyendoId(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        Banco banco = toEntity(dto);
        banco.setIdBanco(id);
        banco.setNombre(nombre);
        banco.setFechaCreacion(existente.getFechaCreacion());
        banco.setUsuarioCreacion(existente.getUsuarioCreacion());
        banco.setFechaModificacion(LocalDateTime.now());
        banco.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        bancoRepository.update(banco);
        return toDTO(banco);
    }

    /**
     * Si el banco esta asociado a cuentas bancarias de empleados, TRG_BANCO_BAJA_VALIDA
     * (ORA-20107) rechaza el DELETE y GlobalExceptionHandler responde 409 con
     * el mensaje del trigger; aqui no se captura nada (D2).
     */
    public void delete(Integer id) {
        int filasAfectadas = bancoRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw noEncontrado(id);
        }
    }

    private ResponseStatusException noEncontrado(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Banco no encontrado con id: " + id);
    }

    private BancoDTO toDTO(Banco banco) {
        BancoDTO dto = new BancoDTO();
        dto.setIdBanco(banco.getIdBanco());
        dto.setNombre(banco.getNombre());
        return dto;
    }

    private Banco toEntity(BancoDTO dto) {
        Banco banco = new Banco();
        banco.setIdBanco(dto.getIdBanco());
        banco.setNombre(dto.getNombre());
        return banco;
    }
}
