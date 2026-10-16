package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.CuentaBancariaEmpleadoDTO;
import com.analisis_sistemas.erp.dto.EmpleadoOpcionDTO;
import com.analisis_sistemas.erp.entity.CuentaBancariaEmpleado;
import com.analisis_sistemas.erp.repository.BancoRepository;
import com.analisis_sistemas.erp.repository.CuentaBancariaEmpleadoRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reglas de negocio (las dos tambien estan como indice UNIQUE en
 * database/09_fase2_andres_cuentas_bancarias.sql, D3):
 * - Un empleado no puede tener dos cuentas ACTIVAS del mismo banco.
 * - Un numero de cuenta no se repite dentro del mismo banco.
 */
@Service
public class CuentaBancariaEmpleadoService {

    /** Id que nunca existe: en el alta no hay registro propio que excluir. */
    private static final int SIN_EXCLUIR = 0;

    private static final String MENSAJE_EMPLEADO = "El empleado seleccionado no existe.";
    private static final String MENSAJE_BANCO = "El banco seleccionado no existe.";
    private static final String MENSAJE_NUMERO_DUPLICADO =
            "Ya existe una cuenta con ese número en el banco seleccionado.";
    private static final String MENSAJE_ACTIVA_DUPLICADA =
            "El empleado ya tiene una cuenta activa en ese banco. Desactívela antes de registrar otra.";

    private final CuentaBancariaEmpleadoRepository cuentaRepository;
    private final BancoRepository bancoRepository;

    public CuentaBancariaEmpleadoService(CuentaBancariaEmpleadoRepository cuentaRepository,
                                         BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    public List<CuentaBancariaEmpleadoDTO> findAll(boolean soloActivas, Integer idEmpleado) {
        return cuentaRepository.findAll(soloActivas, idEmpleado).stream()
                .map(this::toDTO)
                .toList();
    }

    public CuentaBancariaEmpleadoDTO findById(Integer id) {
        return toDTO(buscar(id));
    }

    public List<EmpleadoOpcionDTO> findEmpleados() {
        return cuentaRepository.findEmpleados();
    }

    public CuentaBancariaEmpleadoDTO create(CuentaBancariaEmpleadoDTO dto) {
        CuentaBancariaEmpleado cuenta = toEntity(dto);
        validar(cuenta, SIN_EXCLUIR);

        cuenta.setIdCuentaBancaria(null);
        cuenta.setFechaCreacion(LocalDateTime.now());
        cuenta.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado());

        CuentaBancariaEmpleado guardada = cuentaRepository.save(cuenta);
        return toDTO(buscar(guardada.getIdCuentaBancaria()));
    }

    public CuentaBancariaEmpleadoDTO update(Integer id, CuentaBancariaEmpleadoDTO dto) {
        CuentaBancariaEmpleado existente = buscar(id);

        CuentaBancariaEmpleado cuenta = toEntity(dto);
        validar(cuenta, id);

        cuenta.setIdCuentaBancaria(id);
        cuenta.setFechaCreacion(existente.getFechaCreacion());
        cuenta.setUsuarioCreacion(existente.getUsuarioCreacion());
        cuenta.setFechaModificacion(LocalDateTime.now());
        cuenta.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());

        int filas = cuentaRepository.update(cuenta);
        if (filas == 0) {
            throw noEncontrada(id);
        }
        return toDTO(buscar(id));
    }

    public void delete(Integer id) {
        int filas = cuentaRepository.deleteById(id);
        if (filas == 0) {
            throw noEncontrada(id);
        }
    }

    private void validar(CuentaBancariaEmpleado cuenta, Integer idExcluir) {
        if (!cuentaRepository.existsEmpleado(cuenta.getIdEmpleado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MENSAJE_EMPLEADO);
        }
        if (bancoRepository.findById(cuenta.getIdBanco()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MENSAJE_BANCO);
        }
        if (cuentaRepository.existsNumeroEnBanco(cuenta.getIdBanco(), cuenta.getNumeroDeCuenta(), idExcluir)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_NUMERO_DUPLICADO);
        }
        if (Boolean.TRUE.equals(cuenta.getActiva())
                && cuentaRepository.existsActivaEnBanco(cuenta.getIdEmpleado(), cuenta.getIdBanco(), idExcluir)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, MENSAJE_ACTIVA_DUPLICADA);
        }
    }

    private CuentaBancariaEmpleado buscar(Integer id) {
        return cuentaRepository.findById(id).orElseThrow(() -> noEncontrada(id));
    }

    private ResponseStatusException noEncontrada(Integer id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta bancaria no encontrada con id: " + id);
    }

    private CuentaBancariaEmpleadoDTO toDTO(CuentaBancariaEmpleado cuenta) {
        CuentaBancariaEmpleadoDTO dto = new CuentaBancariaEmpleadoDTO();
        dto.setIdCuentaBancaria(cuenta.getIdCuentaBancaria());
        dto.setIdEmpleado(cuenta.getIdEmpleado());
        dto.setIdBanco(cuenta.getIdBanco());
        dto.setNumeroDeCuenta(cuenta.getNumeroDeCuenta());
        dto.setActiva(cuenta.getActiva());
        dto.setNombreEmpleado(cuenta.getNombreEmpleado());
        dto.setNombrePuesto(cuenta.getNombrePuesto());
        dto.setNombreBanco(cuenta.getNombreBanco());
        return dto;
    }

    private CuentaBancariaEmpleado toEntity(CuentaBancariaEmpleadoDTO dto) {
        CuentaBancariaEmpleado cuenta = new CuentaBancariaEmpleado();
        cuenta.setIdCuentaBancaria(dto.getIdCuentaBancaria());
        cuenta.setIdEmpleado(dto.getIdEmpleado());
        cuenta.setIdBanco(dto.getIdBanco());
        cuenta.setNumeroDeCuenta(dto.getNumeroDeCuenta().trim());
        cuenta.setActiva(dto.getActiva() == null || dto.getActiva());
        return cuenta;
    }
}
