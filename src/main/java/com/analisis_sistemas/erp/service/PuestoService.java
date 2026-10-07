package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.PuestoDTO;
import com.analisis_sistemas.erp.entity.Departamento;
import com.analisis_sistemas.erp.entity.Puesto;
import com.analisis_sistemas.erp.repository.DepartamentoRepository;
import com.analisis_sistemas.erp.repository.PuestoRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PuestoService {

    private static final String MENSAJE_DUPLICADO =
            "Ya existe un puesto con ese nombre en el departamento seleccionado.";

    private final PuestoRepository puestoRepository;
    private final DepartamentoRepository departamentoRepository;

    public PuestoService(PuestoRepository puestoRepository, DepartamentoRepository departamentoRepository) {
        this.puestoRepository = puestoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    public List<PuestoDTO> findAll() {
        return puestoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public PuestoDTO findById(Integer id) {
        Puesto puesto = puestoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));
        return toDTO(puesto);
    }

    public List<PuestoDTO> findByDepartamentoId(Integer idDepartamento) {
        return puestoRepository.findByDepartamentoId(idDepartamento).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PuestoDTO> findByEmpresaId(Integer idEmpresa) {
        return puestoRepository.findByEmpresaId(idEmpresa).stream()
                .map(this::toDTO)
                .toList();
    }

    public PuestoDTO create(PuestoDTO dto) {
        Departamento departamento = buscarDepartamento(dto.getIdDepartamento());

        String nombre = dto.getNombre().trim();
        if (puestoRepository.existsByDepartamentoYNombre(dto.getIdDepartamento(), nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        Puesto puesto = toEntity(dto);
        puesto.setIdPuesto(null);
        puesto.setNombre(nombre);
        puesto.setFechaCreacion(LocalDateTime.now());
        puesto.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Puesto saved = puestoRepository.save(puesto);
        completarJerarquia(saved, departamento);
        return toDTO(saved);
    }

    public PuestoDTO update(Integer id, PuestoDTO dto) {
        Puesto existente = puestoRepository.findById(id)
                .orElseThrow(() -> noEncontrado(id));

        Departamento departamento = buscarDepartamento(dto.getIdDepartamento());

        // Los empleados guardan su sucursal (y con ella su empresa): mover un
        // puesto con empleados a un departamento de otra empresa los dejaria
        // inconsistentes. Moverlo dentro de la misma empresa si se permite.
        if (!Objects.equals(existente.getIdEmpresa(), departamento.getIdEmpresa())) {
            int empleados = puestoRepository.countEmpleados(id);
            if (empleados > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "No se puede mover el puesto a otra empresa: está asignado a " + empleados + " empleado(s).");
            }
        }

        String nombre = dto.getNombre().trim();
        if (puestoRepository.existsByDepartamentoYNombreExcluyendoId(dto.getIdDepartamento(), nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_DUPLICADO);
        }

        Puesto puesto = toEntity(dto);
        puesto.setIdPuesto(id);
        puesto.setNombre(nombre);
        puesto.setFechaCreacion(existente.getFechaCreacion());
        puesto.setUsuarioCreacion(existente.getUsuarioCreacion());
        puesto.setFechaModificacion(LocalDateTime.now());
        puesto.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        puestoRepository.update(puesto);
        completarJerarquia(puesto, departamento);
        return toDTO(puesto);
    }

    /**
     * Si el puesto esta asignado a empleados, aparece en detalles de planilla
     * o en liquidaciones, TRG_PUESTO_BAJA_VALIDA (ORA-20109/-20110/-20111)
     * rechaza el DELETE y GlobalExceptionHandler responde 409 con el mensaje
     * del trigger; aqui no se captura nada (D2).
     */
    public void delete(Integer id) {
        int filasAfectadas = puestoRepository.deleteById(id);
        if (filasAfectadas == 0) {
            throw noEncontrado(id);
        }
    }

    private Departamento buscarDepartamento(Integer idDepartamento) {
        return departamentoRepository.findById(idDepartamento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El departamento seleccionado no existe."));
    }

    private void completarJerarquia(Puesto puesto, Departamento departamento) {
        puesto.setNombreDepartamento(departamento.getNombre());
        puesto.setIdEmpresa(departamento.getIdEmpresa());
        puesto.setNombreEmpresa(departamento.getNombreEmpresa());
    }

    private ResponseStatusException noEncontrado(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Puesto no encontrado con id: " + id);
    }

    private PuestoDTO toDTO(Puesto puesto) {
        PuestoDTO dto = new PuestoDTO();
        dto.setIdPuesto(puesto.getIdPuesto());
        dto.setNombre(puesto.getNombre());
        dto.setIdDepartamento(puesto.getIdDepartamento());
        dto.setNombreDepartamento(puesto.getNombreDepartamento());
        dto.setIdEmpresa(puesto.getIdEmpresa());
        dto.setNombreEmpresa(puesto.getNombreEmpresa());
        return dto;
    }

    /** nombreDepartamento, idEmpresa y nombreEmpresa son solo de salida: no se copian. */
    private Puesto toEntity(PuestoDTO dto) {
        Puesto puesto = new Puesto();
        puesto.setIdPuesto(dto.getIdPuesto());
        puesto.setNombre(dto.getNombre());
        puesto.setIdDepartamento(dto.getIdDepartamento());
        return puesto;
    }
}
