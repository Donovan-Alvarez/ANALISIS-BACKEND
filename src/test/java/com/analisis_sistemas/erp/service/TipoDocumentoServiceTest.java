package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.TipoDocumentoDTO;
import com.analisis_sistemas.erp.entity.TipoDocumento;
import com.analisis_sistemas.erp.repository.TipoDocumentoRepository;
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
class TipoDocumentoServiceTest {

    @Mock
    private TipoDocumentoRepository repository;

    @InjectMocks
    private TipoDocumentoService service;

    private static TipoDocumentoDTO dto(String nombre) {
        return new TipoDocumentoDTO(null, nombre);
    }

    private static TipoDocumento existente(Integer id, String nombre) {
        LocalDateTime creacion = LocalDateTime.of(2026, 1, 15, 8, 30);
        return new TipoDocumento(id, nombre, creacion, "creador", null, null);
    }

    @Test
    void create_nombreDuplicado_lanza409SinGuardar() {
        when(repository.existsByNombre("Pasaporte")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto("Pasaporte")))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).isEqualTo("Ya existe un tipo de documento con ese nombre.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_recortaEspaciosYLlenaAuditoriaDeCreacion() {
        when(repository.existsByNombre("Carné de residente")).thenReturn(false);
        when(repository.save(any(TipoDocumento.class))).thenAnswer(inv -> {
            TipoDocumento e = inv.getArgument(0);
            e.setIdTipoDocumento(10);
            return e;
        });

        TipoDocumentoDTO creado = service.create(dto("  Carné de residente  "));

        ArgumentCaptor<TipoDocumento> captor = ArgumentCaptor.forClass(TipoDocumento.class);
        verify(repository).save(captor.capture());
        TipoDocumento guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Carné de residente");
        assertThat(guardado.getFechaCreacion()).isNotNull();
        assertThat(guardado.getUsuarioCreacion()).isEqualTo("system"); // sin usuario autenticado
        assertThat(guardado.getFechaModificacion()).isNull();
        assertThat(creado.getIdTipoDocumento()).isEqualTo(10);
        assertThat(creado.getNombre()).isEqualTo("Carné de residente");
    }

    @Test
    void update_nombreDeOtroRegistro_lanza409SinActualizar() {
        when(repository.findById(3)).thenReturn(Optional.of(existente(3, "NIT")));
        when(repository.existsByNombreExcluyendoId("Pasaporte", 3)).thenReturn(true);

        assertThatThrownBy(() -> service.update(3, dto("Pasaporte")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(repository, never()).update(any());
    }

    @Test
    void update_mismoNombreDelPropioRegistro_noEsDuplicadoYConservaCreacion() {
        TipoDocumento actual = existente(3, "Licencia de Conducir");
        when(repository.findById(3)).thenReturn(Optional.of(actual));
        when(repository.existsByNombreExcluyendoId("LICENCIA DE CONDUCIR", 3)).thenReturn(false);

        TipoDocumentoDTO actualizado = service.update(3, dto(" LICENCIA DE CONDUCIR "));

        ArgumentCaptor<TipoDocumento> captor = ArgumentCaptor.forClass(TipoDocumento.class);
        verify(repository).update(captor.capture());
        TipoDocumento enviado = captor.getValue();
        assertThat(enviado.getIdTipoDocumento()).isEqualTo(3);
        assertThat(enviado.getNombre()).isEqualTo("LICENCIA DE CONDUCIR");
        assertThat(enviado.getFechaCreacion()).isEqualTo(actual.getFechaCreacion());
        assertThat(enviado.getUsuarioCreacion()).isEqualTo("creador");
        assertThat(enviado.getFechaModificacion()).isNotNull();
        assertThat(enviado.getUsuarioModificacion()).isEqualTo("system");
        assertThat(actualizado.getNombre()).isEqualTo("LICENCIA DE CONDUCIR");
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
