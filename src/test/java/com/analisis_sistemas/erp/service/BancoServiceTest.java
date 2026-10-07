package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.BancoDTO;
import com.analisis_sistemas.erp.entity.Banco;
import com.analisis_sistemas.erp.repository.BancoRepository;
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
class BancoServiceTest {

    @Mock
    private BancoRepository repository;

    @InjectMocks
    private BancoService service;

    private static BancoDTO dto(String nombre) {
        return new BancoDTO(null, nombre);
    }

    private static Banco existente(Integer id, String nombre) {
        LocalDateTime creacion = LocalDateTime.of(2026, 1, 15, 8, 30);
        return new Banco(id, nombre, creacion, "creador", null, null);
    }

    @Test
    void create_nombreDuplicado_lanza409SinGuardar() {
        when(repository.existsByNombre("BANCO INDUSTRIAL")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto("BANCO INDUSTRIAL")))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).isEqualTo("Ya existe un banco con ese nombre.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_recortaEspaciosYLlenaAuditoriaDeCreacion() {
        when(repository.existsByNombre("BANCO PROMERICA")).thenReturn(false);
        when(repository.save(any(Banco.class))).thenAnswer(inv -> {
            Banco e = inv.getArgument(0);
            e.setIdBanco(10);
            return e;
        });

        BancoDTO creado = service.create(dto("  BANCO PROMERICA  "));

        ArgumentCaptor<Banco> captor = ArgumentCaptor.forClass(Banco.class);
        verify(repository).save(captor.capture());
        Banco guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("BANCO PROMERICA");
        assertThat(guardado.getFechaCreacion()).isNotNull();
        assertThat(guardado.getUsuarioCreacion()).isEqualTo("system"); // sin usuario autenticado
        assertThat(guardado.getFechaModificacion()).isNull();
        assertThat(creado.getIdBanco()).isEqualTo(10);
        assertThat(creado.getNombre()).isEqualTo("BANCO PROMERICA");
    }

    @Test
    void update_nombreDeOtroRegistro_lanza409SinActualizar() {
        when(repository.findById(3)).thenReturn(Optional.of(existente(3, "BANCO RURAL")));
        when(repository.existsByNombreExcluyendoId("BANCO CHN", 3)).thenReturn(true);

        assertThatThrownBy(() -> service.update(3, dto("BANCO CHN")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(repository, never()).update(any());
    }

    @Test
    void update_mismoNombreDelPropioRegistro_noEsDuplicadoYConservaCreacion() {
        Banco actual = existente(3, "BANCO RURAL");
        when(repository.findById(3)).thenReturn(Optional.of(actual));
        when(repository.existsByNombreExcluyendoId("Banco Rural", 3)).thenReturn(false);

        BancoDTO actualizado = service.update(3, dto(" Banco Rural "));

        ArgumentCaptor<Banco> captor = ArgumentCaptor.forClass(Banco.class);
        verify(repository).update(captor.capture());
        Banco enviado = captor.getValue();
        assertThat(enviado.getIdBanco()).isEqualTo(3);
        assertThat(enviado.getNombre()).isEqualTo("Banco Rural");
        assertThat(enviado.getFechaCreacion()).isEqualTo(actual.getFechaCreacion());
        assertThat(enviado.getUsuarioCreacion()).isEqualTo("creador");
        assertThat(enviado.getFechaModificacion()).isNotNull();
        assertThat(enviado.getUsuarioModificacion()).isEqualTo("system");
        assertThat(actualizado.getNombre()).isEqualTo("Banco Rural");
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
