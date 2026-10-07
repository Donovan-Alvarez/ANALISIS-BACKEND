package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.DepartamentoDTO;
import com.analisis_sistemas.erp.entity.Departamento;
import com.analisis_sistemas.erp.entity.Empresa;
import com.analisis_sistemas.erp.repository.DepartamentoRepository;
import com.analisis_sistemas.erp.repository.EmpresaRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DepartamentoService {

    private static final String MENSAJE_DUPLICADO =
            "Ya existe un departamento con ese nombre en la empresa seleccionada.";

    private final DepartamentoRepository departamentoRepository;
    private final EmpresaRepository empresaRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository, EmpresaRepository empresaRepository) {
        this.departamentoRepository = departamentoRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<DepartamentoDTO> findAll() {
        return departamentoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public DepartamentoDTO findById(Integer id) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
        return toDTO(departamento);
    }

    public List<DepartamentoDTO> findByEmpresaId(Integer idEmpresa) {
        return departamentoRepository.findByEmpresaId(idEmpresa).stream()
                .map(this::toDTO)
                .toList();
    }

    public DepartamentoDTO create(DepartamentoDTO dto) {
        Empresa empresa = buscarEmpresa(dto.getIdEmpresa());

        String nombre = dto.getNombre().trim();
        if (departamentoRepository.existsByEmpresaYNombre(dto.getIdEmpresa(), nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        Departamento departamento = toEntity(dto);
        departamento.setIdDepartamento(null);
        departamento.setNombre(nombre);
        departamento.setFechaCreacion(LocalDateTime.now());
        departamento.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Departamento saved = departamentoRepository.save(departamento);
        saved.setNombreEmpresa(empresa.getNombre());
        return toDTO(saved);
    }

    public DepartamentoDTO update(Integer id, DepartamentoDTO dto) {
        Departamento existente = departamentoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));

        Empresa empresa = buscarEmpresa(dto.getIdEmpresa());

        // Los empleados guardan su sucursal (y con ella su empresa) y su
        // puesto (empresa via departamento): mover un departamento con
        // puestos a otra empresa los dejaria inconsistentes.
        if (!Objects.equals(existente.getIdEmpresa(), dto.getIdEmpresa())) {
            int puestos = departamentoRepository.countPuestos(id);
            if (puestos > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "No se puede cambiar la empresa del departamento: tiene " + puestos + " puesto(s) asociado(s).");
            }
        }

        String nombre = dto.getNombre().trim();
        if (departamentoRepository.existsByEmpresaYNombreExcluyendoId(dto.getIdEmpresa(), nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        Departamento departamento = toEntity(dto);
        departamento.setIdDepartamento(id);
        departamento.setNombre(nombre);
        departamento.setFechaCreacion(existente.getFechaCreacion());
        departamento.setUsuarioCreacion(existente.getUsuarioCreacion());
        departamento.setFechaModificacion(LocalDateTime.now());
        departamento.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        departamentoRepository.update(departamento);
        departamento.setNombreEmpresa(empresa.getNombre());
        return toDTO(departamento);
    }

    /**
     * Si el departamento tiene puestos, TRG_DEPARTAMENTO_BAJA_VALIDA (ORA-20108)
     * rechaza el DELETE y GlobalExceptionHandler responde 409 con el mensaje
     * del trigger; aqui no se captura nada (D2).
     */
    public void delete(Integer id) {
        int filasAfectadas = departamentoRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw noEncontrado(id);
        }
    }

    private Empresa buscarEmpresa(Integer idEmpresa) {
        return empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La empresa seleccionada no existe."));
    }

    private ResponseStatusException noEncontrado(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento no encontrado con id: " + id);
    }

    private DepartamentoDTO toDTO(Departamento departamento) {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setIdDepartamento(departamento.getIdDepartamento());
        dto.setNombre(departamento.getNombre());
        dto.setIdEmpresa(departamento.getIdEmpresa());
        dto.setNombreEmpresa(departamento.getNombreEmpresa());
        return dto;
    }

    /** nombreEmpresa es solo de salida: no se copia. */
    private Departamento toEntity(DepartamentoDTO dto) {
        Departamento departamento = new Departamento();
        departamento.setIdDepartamento(dto.getIdDepartamento());
        departamento.setNombre(dto.getNombre());
        departamento.setIdEmpresa(dto.getIdEmpresa());
        return departamento;
    }
}
