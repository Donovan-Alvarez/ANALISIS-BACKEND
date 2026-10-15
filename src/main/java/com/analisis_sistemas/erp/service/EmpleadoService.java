package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.EmpleadoDTO;
import com.analisis_sistemas.erp.dto.PersonaComboDTO;
import com.analisis_sistemas.erp.dto.StatusPermitidoDTO;
import com.analisis_sistemas.erp.entity.Empleado;
import com.analisis_sistemas.erp.entity.StatusEmpleado;
import com.analisis_sistemas.erp.repository.EmpleadoRepository;
import com.analisis_sistemas.erp.repository.PuestoRepository;
import com.analisis_sistemas.erp.repository.StatusEmpleadoRepository;
import com.analisis_sistemas.erp.repository.SucursalRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final PuestoRepository puestoRepository;
    private final StatusEmpleadoRepository statusEmpleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository,
                            SucursalRepository sucursalRepository,
                            PuestoRepository puestoRepository,
                            StatusEmpleadoRepository statusEmpleadoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.sucursalRepository = sucursalRepository;
        this.puestoRepository = puestoRepository;
        this.statusEmpleadoRepository = statusEmpleadoRepository;
    }

    public List<EmpleadoDTO> findAll() {
        return empleadoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public EmpleadoDTO findById(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    /** Alimenta el combo de status de la pantalla con las transiciones validas. */
    public List<StatusPermitidoDTO> findStatusPermitidos(Integer id) {
        buscarOFallar(id);
        return empleadoRepository.findStatusPermitidos(id);
    }

    /**
     * Personas para el combo de la pantalla.
     * PROVISIONAL: mover al endpoint de la pantalla de Personas cuando exista.
     */
    public List<PersonaComboDTO> findPersonasCombo() {
        return empleadoRepository.findPersonasCombo();
    }

    public EmpleadoDTO create(EmpleadoDTO dto) {
        validarCatalogos(dto);

        Empleado empleado = toEntity(dto);
        empleado.setIdEmpleado(null);
        empleado.setFechaCreacion(LocalDateTime.now());
        empleado.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        Integer idGenerado = empleadoRepository.save(empleado);

        // Se relee para devolver los nombres de los JOIN ya resueltos, en vez
        // de armarlos a mano y arriesgar que no coincidan con la BD.
        return findById(idGenerado);
    }

    public EmpleadoDTO update(Integer id, EmpleadoDTO dto) {
        Empleado existente = buscarOFallar(id);
        validarCatalogos(dto);
        validarTransicionDeStatus(existente, dto.getIdStatusEmpleado());

        Empleado empleado = toEntity(dto);
        empleado.setIdEmpleado(id);
        empleado.setFechaModificacion(LocalDateTime.now());
        empleado.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        empleadoRepository.update(empleado);
        return findById(id);
    }

    /**
     * No se captura nada: PR_EMPLEADO_ELIMINAR rechaza con ORA-20204/-20205/
     * -20206 si el empleado tiene liquidacion, inasistencias o cuentas, y con
     * ORA-20207 si no existe. GlobalExceptionHandler los traduce a 409 con el
     * mensaje del procedimiento, que ya esta escrito para el usuario final.
     */
    public void delete(Integer id) {
        empleadoRepository.deleteById(id);
    }

    /**
     * Regla central de la pantalla: un cambio de status solo vale si esta
     * configurado en FLUJO_STATUS_EMPLEADO (catalogo de la pantalla "Flujos
     * Status Empleado"). Quedarse en el mismo status no requiere transicion.
     * <p>
     * La comprobacion se repite dentro de PR_EMPLEADO_ACTUALIZAR; aqui se
     * hace primero para poder nombrar los dos status en el mensaje.
     */
    private void validarTransicionDeStatus(Empleado existente, Integer idStatusNuevo) {
        if (Objects.equals(existente.getIdStatusEmpleado(), idStatusNuevo)) {
            return;
        }

        if (!empleadoRepository.esTransicionValida(existente.getIdEmpleado(), idStatusNuevo)) {
            String nombreNuevo = statusEmpleadoRepository.findById(idStatusNuevo)
                    .map(StatusEmpleado::getNombre)
                    .orElse("el status seleccionado");

            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede cambiar el status de \"" + existente.getNombreStatus()
                            + "\" a \"" + nombreNuevo + "\": esa transición no está configurada "
                            + "en Flujos Status Empleado.");
        }
    }

    /**
     * Valida los catalogos que tienen repositorio propio para dar un mensaje
     * preciso. La persona no se valida aqui porque PersonaRepository todavia
     * no existe (pantalla de Andrea): si el id no existe, la FK de EMPLEADO
     * lo rechaza y GlobalExceptionHandler responde 400 con "El registro
     * relacionado seleccionado no existe.". Cuando esa pantalla este lista,
     * agregar aqui la validacion de IdPersona.
     */
    private void validarCatalogos(EmpleadoDTO dto) {
        if (sucursalRepository.findById(dto.getIdSucursal()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sucursal seleccionada no existe.");
        }
        if (puestoRepository.findById(dto.getIdPuesto()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El puesto seleccionado no existe.");
        }
        if (statusEmpleadoRepository.findById(dto.getIdStatusEmpleado()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El status de empleado seleccionado no existe.");
        }
    }

    private Empleado buscarOFallar(Integer id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Empleado no encontrado con id: " + id));
    }

    private EmpleadoDTO toDTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setIdPersona(empleado.getIdPersona());
        dto.setIdSucursal(empleado.getIdSucursal());
        dto.setIdPuesto(empleado.getIdPuesto());
        dto.setIdStatusEmpleado(empleado.getIdStatusEmpleado());
        dto.setFechaContratacion(empleado.getFechaContratacion());
        dto.setIngresoSueldoBase(empleado.getIngresoSueldoBase());
        dto.setIngresoBonificacionDecreto(empleado.getIngresoBonificacionDecreto());
        dto.setIngresoOtrosIngresos(empleado.getIngresoOtrosIngresos());
        dto.setDescuentoIgss(empleado.getDescuentoIgss());
        dto.setDescuentoIsr(empleado.getDescuentoIsr());
        dto.setDescuentoInasistencias(empleado.getDescuentoInasistencias());
        dto.setNombrePersona(empleado.getNombrePersona());
        dto.setNombreSucursal(empleado.getNombreSucursal());
        dto.setNombrePuesto(empleado.getNombrePuesto());
        dto.setIdDepartamento(empleado.getIdDepartamento());
        dto.setNombreDepartamento(empleado.getNombreDepartamento());
        dto.setNombreStatus(empleado.getNombreStatus());
        dto.setFechaCreacion(empleado.getFechaCreacion());
        dto.setUsuarioCreacion(empleado.getUsuarioCreacion());
        dto.setFechaModificacion(empleado.getFechaModificacion());
        dto.setUsuarioModificacion(empleado.getUsuarioModificacion());
        return dto;
    }

    /** Los nombres de los JOIN son solo de salida: no se copian. */
    private Empleado toEntity(EmpleadoDTO dto) {
        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(dto.getIdEmpleado());
        empleado.setIdPersona(dto.getIdPersona());
        empleado.setIdSucursal(dto.getIdSucursal());
        empleado.setIdPuesto(dto.getIdPuesto());
        empleado.setIdStatusEmpleado(dto.getIdStatusEmpleado());
        empleado.setFechaContratacion(dto.getFechaContratacion());
        empleado.setIngresoSueldoBase(EmpleadoRepository.valorOCero(dto.getIngresoSueldoBase()));
        empleado.setIngresoBonificacionDecreto(EmpleadoRepository.valorOCero(dto.getIngresoBonificacionDecreto()));
        empleado.setIngresoOtrosIngresos(EmpleadoRepository.valorOCero(dto.getIngresoOtrosIngresos()));
        empleado.setDescuentoIgss(EmpleadoRepository.valorOCero(dto.getDescuentoIgss()));
        empleado.setDescuentoIsr(EmpleadoRepository.valorOCero(dto.getDescuentoIsr()));
        empleado.setDescuentoInasistencias(EmpleadoRepository.valorOCero(dto.getDescuentoInasistencias()));
        return empleado;
    }
}
