package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.EstadoCivilDTO;
import com.analisis_sistemas.erp.entity.EstadoCivil;
import com.analisis_sistemas.erp.repository.EstadoCivilRepository;
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
class EstadoCivilServiceTest {

    @Mock
    private EstadoCivilRepository repository;

    @InjectMocks
    private EstadoCivilService service;

    private static EstadoCivilDTO dto(String nombre) {
        return new EstadoCivilDTO(null, nombre);
    }

    private static EstadoCivil existente(Integer id, String nombre) {
        LocalDateTime creacion = LocalDateTime.of(2026, 1, 15, 8, 30);
        return new EstadoCivil(id, nombre, creacion, "creador", null, null);
    }

    @Test
    void create_nombreDuplicado_lanza409SinGuardar() {
        when(repository.existsByNombre("Soltero(a)")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto("Soltero(a)")))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).isEqualTo("Ya existe un estado civil con ese nombre.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_recortaEspaciosYLlenaAuditoriaDeCreacion() {
        when(repository.existsByNombre("Separado(a)")).thenReturn(false);
        when(repository.save(any(EstadoCivil.class))).thenAnswer(inv -> {
            EstadoCivil e = inv.getArgument(0);
            e.setIdEstadoCivil(10);
            return e;
        });

        EstadoCivilDTO creado = service.create(dto("  Separado(a)  "));

        ArgumentCaptor<EstadoCivil> captor = ArgumentCaptor.forClass(EstadoCivil.class);
        verify(repository).save(captor.capture());
        EstadoCivil guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Separado(a)");
        assertThat(guardado.getFechaCreacion()).isNotNull();
        assertThat(guardado.getUsuarioCreacion()).isEqualTo("system"); // sin usuario autenticado
        assertThat(guardado.getFechaModificacion()).isNull();
        assertThat(creado.getIdEstadoCivil()).isEqualTo(10);
        assertThat(creado.getNombre()).isEqualTo("Separado(a)");
    }

    @Test
    void update_nombreDeOtroRegistro_lanza409SinActualizar() {
        when(repository.findById(3)).thenReturn(Optional.of(existente(3, "Divorciado(a)")));
        when(repository.existsByNombreExcluyendoId("Casado(a)", 3)).thenReturn(true);

        assertThatThrownBy(() -> service.update(3, dto("Casado(a)")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(repository, never()).update(any());
    }

    @Test
    void update_mismoNombreDelPropioRegistro_noEsDuplicadoYConservaCreacion() {
        EstadoCivil actual = existente(3, "Divorciado(a)");
        when(repository.findById(3)).thenReturn(Optional.of(actual));
        when(repository.existsByNombreExcluyendoId("DIVORCIADO(A)", 3)).thenReturn(false);

        EstadoCivilDTO actualizado = service.update(3, dto(" DIVORCIADO(A) "));

        ArgumentCaptor<EstadoCivil> captor = ArgumentCaptor.forClass(EstadoCivil.class);
        verify(repository).update(captor.capture());
        EstadoCivil enviado = captor.getValue();
        assertThat(enviado.getIdEstadoCivil()).isEqualTo(3);
        assertThat(enviado.getNombre()).isEqualTo("DIVORCIADO(A)");
        assertThat(enviado.getFechaCreacion()).isEqualTo(actual.getFechaCreacion());
        assertThat(enviado.getUsuarioCreacion()).isEqualTo("creador");
        assertThat(enviado.getFechaModificacion()).isNotNull();
        assertThat(enviado.getUsuarioModificacion()).isEqualTo("system");
        assertThat(actualizado.getNombre()).isEqualTo("DIVORCIADO(A)");
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
