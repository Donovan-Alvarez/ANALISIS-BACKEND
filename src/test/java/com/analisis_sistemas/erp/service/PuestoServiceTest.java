package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.PuestoDTO;
import com.analisis_sistemas.erp.entity.Departamento;
import com.analisis_sistemas.erp.entity.Puesto;
import com.analisis_sistemas.erp.repository.DepartamentoRepository;
import com.analisis_sistemas.erp.repository.PuestoRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Prueba unitaria de las reglas del Service (duplicado por departamento,
 * departamento existente, mover a otra empresa con empleados, trim,
 * auditoria D1), con los repositorios simulados: sin base de datos ni
 * contexto Spring.
 */
@ExtendWith(MockitoExtension.class)
class PuestoServiceTest {

    private static final int SOFTWARE_INC = 1;
    private static final int SECURITY = 42;

    @Mock
    private PuestoRepository repository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private PuestoService service;

    private static PuestoDTO dto(String nombre, Integer idDepartamento) {
        return new PuestoDTO(null, nombre, idDepartamento, null, null, null);
    }

    private static Departamento departamento(int id, String nombre, int idEmpresa, String nombreEmpresa) {
        return new Departamento(id, nombre, idEmpresa, nombreEmpresa, null, "system", null, null);
    }

    private static final Departamento FINANZAS = departamento(5, "Finanzas", SOFTWARE_INC, "Software Inc.");
    private static final Departamento ADMINISTRACION = departamento(1, "Administración", SOFTWARE_INC, "Software Inc.");
    private static final Departamento PRUEBA_SECURITY = departamento(30, "Departamento de prueba", SECURITY, "Security");

    private static Puesto existente(Integer id, String nombre, Departamento depto) {
        LocalDateTime creacion = LocalDateTime.of(2026, 1, 15, 8, 30);
        return new Puesto(id, nombre, depto.getIdDepartamento(), depto.getNombre(), depto.getIdEmpresa(),
                depto.getNombreEmpresa(), creacion, "creador", null, null);
    }

    @Test
    void create_nombreDuplicadoEnElMismoDepartamento_lanza409SinGuardar() {
        when(departamentoRepository.findById(5)).thenReturn(Optional.of(FINANZAS));
        when(repository.existsByDepartamentoYNombre(5, "Contador")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto("Contador", 5)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).isEqualTo("Ya existe un puesto con ese nombre en el departamento seleccionado.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_mismoNombreEnOtroDepartamento_seGuardaConSuJerarquia() {
        when(departamentoRepository.findById(30)).thenReturn(Optional.of(PRUEBA_SECURITY));
        when(repository.existsByDepartamentoYNombre(30, "Contador")).thenReturn(false);
        when(repository.save(any(Puesto.class))).thenAnswer(inv -> {
            Puesto p = inv.getArgument(0);
            p.setIdPuesto(40);
            return p;
        });

        PuestoDTO creado = service.create(dto("Contador", 30));

        assertThat(creado.getIdPuesto()).isEqualTo(40);
        assertThat(creado.getIdDepartamento()).isEqualTo(30);
        assertThat(creado.getNombreDepartamento()).isEqualTo("Departamento de prueba");
        assertThat(creado.getIdEmpresa()).isEqualTo(SECURITY);
        assertThat(creado.getNombreEmpresa()).isEqualTo("Security");
    }

    @Test
    void create_departamentoInexistente_lanza400SinGuardar() {
        when(departamentoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto("Contador", 99)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).isEqualTo("El departamento seleccionado no existe.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_recortaEspaciosYLlenaAuditoria_ignorandoEmpresaDeEntrada() {
        when(departamentoRepository.findById(5)).thenReturn(Optional.of(FINANZAS));
        when(repository.existsByDepartamentoYNombre(5, "Tesorero")).thenReturn(false);
        when(repository.save(any(Puesto.class))).thenAnswer(inv -> inv.getArgument(0));

        // idEmpresa/nombreEmpresa de entrada son de solo lectura: se ignoran.
        PuestoDTO entrada = new PuestoDTO(null, "  Tesorero  ", 5, "otro", SECURITY, "Security");
        PuestoDTO creado = service.create(entrada);

        ArgumentCaptor<Puesto> captor = ArgumentCaptor.forClass(Puesto.class);
        verify(repository).save(captor.capture());
        Puesto guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Tesorero");
        assertThat(guardado.getIdDepartamento()).isEqualTo(5);
        assertThat(guardado.getFechaCreacion()).isNotNull();
        assertThat(guardado.getUsuarioCreacion()).isEqualTo("system"); // sin usuario autenticado
        assertThat(guardado.getFechaModificacion()).isNull();
        assertThat(creado.getIdEmpresa()).isEqualTo(SOFTWARE_INC);
        assertThat(creado.getNombreDepartamento()).isEqualTo("Finanzas");
    }

    @Test
    void update_nombreDeOtroPuestoDelMismoDepartamento_lanza409SinActualizar() {
        when(repository.findById(11)).thenReturn(Optional.of(existente(11, "Contador", FINANZAS)));
        when(departamentoRepository.findById(5)).thenReturn(Optional.of(FINANZAS));
        when(repository.existsByDepartamentoYNombreExcluyendoId(5, "Analista Financiero", 11)).thenReturn(true);

        assertThatThrownBy(() -> service.update(11, dto("Analista Financiero", 5)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(repository, never()).update(any());
    }

    @Test
    void update_mismoNombreDelPropioRegistro_noEsDuplicadoYConservaCreacion() {
        Puesto actual = existente(11, "Contador", FINANZAS);
        when(repository.findById(11)).thenReturn(Optional.of(actual));
        when(departamentoRepository.findById(5)).thenReturn(Optional.of(FINANZAS));
        when(repository.existsByDepartamentoYNombreExcluyendoId(5, "CONTADOR", 11)).thenReturn(false);

        service.update(11, dto(" CONTADOR ", 5));

        ArgumentCaptor<Puesto> captor = ArgumentCaptor.forClass(Puesto.class);
        verify(repository).update(captor.capture());
        Puesto enviado = captor.getValue();
        assertThat(enviado.getIdPuesto()).isEqualTo(11);
        assertThat(enviado.getNombre()).isEqualTo("CONTADOR");
        assertThat(enviado.getFechaCreacion()).isEqualTo(actual.getFechaCreacion());
        assertThat(enviado.getUsuarioCreacion()).isEqualTo("creador");
        assertThat(enviado.getFechaModificacion()).isNotNull();
        assertThat(enviado.getUsuarioModificacion()).isEqualTo("system");
        verify(repository, never()).countEmpleados(anyInt());
    }

    @Test
    void update_moverADepartamentoDeLaMismaEmpresa_sePermiteSinContarEmpleados() {
        when(repository.findById(11)).thenReturn(Optional.of(existente(11, "Contador", FINANZAS)));
        when(departamentoRepository.findById(1)).thenReturn(Optional.of(ADMINISTRACION));
        when(repository.existsByDepartamentoYNombreExcluyendoId(1, "Contador", 11)).thenReturn(false);

        PuestoDTO actualizado = service.update(11, dto("Contador", 1));

        assertThat(actualizado.getIdDepartamento()).isEqualTo(1);
        assertThat(actualizado.getNombreDepartamento()).isEqualTo("Administración");
        verify(repository, never()).countEmpleados(anyInt());
        verify(repository).update(any(Puesto.class));
    }

    @Test
    void update_moverAOtraEmpresaConEmpleados_lanza409SinActualizar() {
        when(repository.findById(11)).thenReturn(Optional.of(existente(11, "Contador", FINANZAS)));
        when(departamentoRepository.findById(30)).thenReturn(Optional.of(PRUEBA_SECURITY));
        when(repository.countEmpleados(11)).thenReturn(5);

        assertThatThrownBy(() -> service.update(11, dto("Contador", 30)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason())
                            .isEqualTo("No se puede mover el puesto a otra empresa: está asignado a 5 empleado(s).");
                });
        verify(repository, never()).update(any());
    }

    @Test
    void update_moverAOtraEmpresaSinEmpleados_sePermite() {
        when(repository.findById(40)).thenReturn(Optional.of(existente(40, "Puesto de prueba", FINANZAS)));
        when(departamentoRepository.findById(30)).thenReturn(Optional.of(PRUEBA_SECURITY));
        when(repository.countEmpleados(40)).thenReturn(0);
        when(repository.existsByDepartamentoYNombreExcluyendoId(30, "Puesto de prueba", 40)).thenReturn(false);

        PuestoDTO actualizado = service.update(40, dto("Puesto de prueba", 30));

        assertThat(actualizado.getIdEmpresa()).isEqualTo(SECURITY);
        verify(repository).update(any(Puesto.class));
    }

    @Test
    void update_idInexistente_lanza404() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, dto("Lo que sea", 5)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void delete_idInexistente_lanza404() {
        when(repository.deleteById(99)).thenReturn(0);

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).isEqualTo("Puesto no encontrado con id: 99");
                });
    }
}
