package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.StatusEmpleadoDTO;
import com.analisis_sistemas.erp.entity.StatusEmpleado;
import com.analisis_sistemas.erp.repository.StatusEmpleadoRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Prueba unitaria de las reglas del Service (duplicados, trim, auditoria D1),
 * con el repositorio simulado: sin base de datos ni contexto Spring.
 */
@ExtendWith(MockitoExtension.class)
class StatusEmpleadoServiceTest {

    @Mock
    private StatusEmpleadoRepository repository;

    @InjectMocks
    private StatusEmpleadoService service;

    private static StatusEmpleadoDTO dto(String nombre) {
        return new StatusEmpleadoDTO(null, nombre);
    }

    private static StatusEmpleado existente(Integer id, String nombre) {
        LocalDateTime creacion = LocalDateTime.of(2026, 1, 15, 8, 30);
        return new StatusEmpleado(id, nombre, creacion, "creador", null, null);
    }

    @Test
    void create_nombreDuplicado_lanza409SinGuardar() {
        when(repository.existsByNombre("Activo")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto("Activo")))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).isEqualTo("Ya existe un status de empleado con ese nombre.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_recortaEspaciosYLlenaAuditoriaDeCreacion() {
        when(repository.existsByNombre("Jubilado")).thenReturn(false);
        when(repository.save(any(StatusEmpleado.class))).thenAnswer(inv -> {
            StatusEmpleado e = inv.getArgument(0);
            e.setIdStatusEmpleado(10);
            return e;
        });

        StatusEmpleadoDTO creado = service.create(dto("  Jubilado  "));

        ArgumentCaptor<StatusEmpleado> captor = ArgumentCaptor.forClass(StatusEmpleado.class);
        verify(repository).save(captor.capture());
        StatusEmpleado guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Jubilado");
        assertThat(guardado.getFechaCreacion()).isNotNull();
        assertThat(guardado.getUsuarioCreacion()).isEqualTo("system"); // sin usuario autenticado
        assertThat(guardado.getFechaModificacion()).isNull();
        assertThat(creado.getIdStatusEmpleado()).isEqualTo(10);
        assertThat(creado.getNombre()).isEqualTo("Jubilado");
    }

    @Test
    void update_nombreDeOtroRegistro_lanza409SinActualizar() {
        when(repository.findById(3)).thenReturn(Optional.of(existente(3, "Baja")));
        when(repository.existsByNombreExcluyendoId("Despedido", 3)).thenReturn(true);

        assertThatThrownBy(() -> service.update(3, dto("Despedido")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(repository, never()).update(any());
    }

    @Test
    void update_mismoNombreDelPropioRegistro_noEsDuplicadoYConservaCreacion() {
        StatusEmpleado actual = existente(3, "Baja");
        when(repository.findById(3)).thenReturn(Optional.of(actual));
        when(repository.existsByNombreExcluyendoId("BAJA", 3)).thenReturn(false);

        StatusEmpleadoDTO actualizado = service.update(3, dto(" BAJA "));

        ArgumentCaptor<StatusEmpleado> captor = ArgumentCaptor.forClass(StatusEmpleado.class);
        verify(repository).update(captor.capture());
        StatusEmpleado enviado = captor.getValue();
        assertThat(enviado.getIdStatusEmpleado()).isEqualTo(3);
        assertThat(enviado.getNombre()).isEqualTo("BAJA");
        assertThat(enviado.getFechaCreacion()).isEqualTo(actual.getFechaCreacion());
        assertThat(enviado.getUsuarioCreacion()).isEqualTo("creador");
        assertThat(enviado.getFechaModificacion()).isNotNull();
        assertThat(enviado.getUsuarioModificacion()).isEqualTo("system");
        assertThat(actualizado.getNombre()).isEqualTo("BAJA");
    }

    @Test
    void update_idInexistente_lanza404() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, dto("Lo que sea")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void delete_idInexistente_lanza404() {
        when(repository.deleteById(99)).thenReturn(0);

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
