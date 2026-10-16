package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.CuentaBancariaEmpleadoDTO;
import com.analisis_sistemas.erp.entity.Banco;
import com.analisis_sistemas.erp.entity.CuentaBancariaEmpleado;
import com.analisis_sistemas.erp.repository.BancoRepository;
import com.analisis_sistemas.erp.repository.CuentaBancariaEmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Reglas del Service de Cuentas Bancarias (empleado/banco existentes, numero
 * unico por banco, una sola activa por empleado y banco, auditoria D1), con
 * los repositorios simulados.
 */
@ExtendWith(MockitoExtension.class)
class CuentaBancariaEmpleadoServiceTest {

    @Mock
    private CuentaBancariaEmpleadoRepository repository;

    @Mock
    private BancoRepository bancoRepository;

    @InjectMocks
    private CuentaBancariaEmpleadoService service;

    private static CuentaBancariaEmpleadoDTO dto(String numero, Boolean activa) {
        CuentaBancariaEmpleadoDTO dto = new CuentaBancariaEmpleadoDTO();
        dto.setIdEmpleado(5);
        dto.setIdBanco(2);
        dto.setNumeroDeCuenta(numero);
        dto.setActiva(activa);
        return dto;
    }

    private static CuentaBancariaEmpleado existente(Integer id) {
        CuentaBancariaEmpleado cuenta = new CuentaBancariaEmpleado();
        cuenta.setIdCuentaBancaria(id);
        cuenta.setIdEmpleado(5);
        cuenta.setIdBanco(2);
        cuenta.setNumeroDeCuenta("002-123456");
        cuenta.setActiva(true);
        cuenta.setFechaCreacion(LocalDateTime.of(2026, 1, 15, 8, 30));
        cuenta.setUsuarioCreacion("creador");
        return cuenta;
    }

    private void empleadoYBancoExisten() {
        when(repository.existsEmpleado(5)).thenReturn(true);
        when(bancoRepository.findById(2)).thenReturn(Optional.of(new Banco()));
    }

    @Test
    void create_empleadoInexistente_lanza400SinGuardar() {
        when(repository.existsEmpleado(5)).thenReturn(false);

        assertThatThrownBy(() -> service.create(dto("002-123456", true)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(repository, never()).save(any());
    }

    @Test
    void create_bancoInexistente_lanza400SinGuardar() {
        when(repository.existsEmpleado(5)).thenReturn(true);
        when(bancoRepository.findById(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto("002-123456", true)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).isEqualTo("El banco seleccionado no existe.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_numeroRepetidoEnElBanco_lanza409() {
        empleadoYBancoExisten();
        when(repository.existsNumeroEnBanco(2, "002-123456", 0)).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto("002-123456", true)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).isEqualTo("Ya existe una cuenta con ese número en el banco seleccionado.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_segundaCuentaActivaEnElMismoBanco_lanza409() {
        empleadoYBancoExisten();
        when(repository.existsNumeroEnBanco(2, "002-999999", 0)).thenReturn(false);
        when(repository.existsActivaEnBanco(5, 2, 0)).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto("002-999999", true)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).startsWith("El empleado ya tiene una cuenta activa en ese banco");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_cuentaInactivaEnBancoConOtraActiva_sePermite() {
        empleadoYBancoExisten();
        when(repository.existsNumeroEnBanco(2, "002-999999", 0)).thenReturn(false);
        when(repository.save(any(CuentaBancariaEmpleado.class))).thenAnswer(inv -> {
            CuentaBancariaEmpleado e = inv.getArgument(0);
            e.setIdCuentaBancaria(50);
            return e;
        });
        when(repository.findById(50)).thenReturn(Optional.of(existente(50)));

        service.create(dto("002-999999", false));

        verify(repository, never()).existsActivaEnBanco(anyInt(), anyInt(), anyInt());
        ArgumentCaptor<CuentaBancariaEmpleado> captor = ArgumentCaptor.forClass(CuentaBancariaEmpleado.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getActiva()).isFalse();
    }

    @Test
    void create_recortaNumeroActivaPorDefectoYLlenaAuditoria() {
        empleadoYBancoExisten();
        when(repository.existsNumeroEnBanco(2, "002-123456", 0)).thenReturn(false);
        when(repository.existsActivaEnBanco(5, 2, 0)).thenReturn(false);
        when(repository.save(any(CuentaBancariaEmpleado.class))).thenAnswer(inv -> {
            CuentaBancariaEmpleado e = inv.getArgument(0);
            e.setIdCuentaBancaria(50);
            return e;
        });
        when(repository.findById(50)).thenReturn(Optional.of(existente(50)));

        CuentaBancariaEmpleadoDTO creada = service.create(dto("  002-123456  ", null));

        ArgumentCaptor<CuentaBancariaEmpleado> captor = ArgumentCaptor.forClass(CuentaBancariaEmpleado.class);
        verify(repository).save(captor.capture());
        CuentaBancariaEmpleado guardada = captor.getValue();
        assertThat(guardada.getNumeroDeCuenta()).isEqualTo("002-123456");
        assertThat(guardada.getActiva()).isTrue();
        assertThat(guardada.getFechaCreacion()).isNotNull();
        assertThat(guardada.getUsuarioCreacion()).isEqualTo("system");
        assertThat(guardada.getFechaModificacion()).isNull();
        assertThat(creada.getIdCuentaBancaria()).isEqualTo(50);
    }

    @Test
    void update_excluyeElPropioRegistroYConservaAuditoriaDeCreacion() {
        when(repository.findById(7)).thenReturn(Optional.of(existente(7)));
        empleadoYBancoExisten();
        when(repository.existsNumeroEnBanco(2, "002-123456", 7)).thenReturn(false);
        when(repository.existsActivaEnBanco(5, 2, 7)).thenReturn(false);
        when(repository.update(any(CuentaBancariaEmpleado.class))).thenReturn(1);

        service.update(7, dto("002-123456", true));

        ArgumentCaptor<CuentaBancariaEmpleado> captor = ArgumentCaptor.forClass(CuentaBancariaEmpleado.class);
        verify(repository).update(captor.capture());
        CuentaBancariaEmpleado actualizada = captor.getValue();
        assertThat(actualizada.getIdCuentaBancaria()).isEqualTo(7);
        assertThat(actualizada.getFechaCreacion()).isEqualTo(LocalDateTime.of(2026, 1, 15, 8, 30));
        assertThat(actualizada.getUsuarioCreacion()).isEqualTo("creador");
        assertThat(actualizada.getFechaModificacion()).isNotNull();
        assertThat(actualizada.getUsuarioModificacion()).isEqualTo("system");
    }

    @Test
    void update_inexistente_lanza404SinValidar() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, dto("002-123456", true)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(repository, never()).existsNumeroEnBanco(anyInt(), anyString(), anyInt());
        verify(repository, never()).update(any());
    }

    @Test
    void delete_inexistente_lanza404() {
        when(repository.deleteById(99)).thenReturn(0);

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
