package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.DepartamentoDTO;
import com.analisis_sistemas.erp.entity.Departamento;
import com.analisis_sistemas.erp.entity.Empresa;
import com.analisis_sistemas.erp.repository.DepartamentoRepository;
import com.analisis_sistemas.erp.repository.EmpresaRepository;
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
 * Prueba unitaria de las reglas del Service (duplicado por empresa, empresa
 * obligatoria y existente, cambio de empresa con puestos, trim, auditoria D1),
 * con los repositorios simulados: sin base de datos ni contexto Spring.
 */
@ExtendWith(MockitoExtension.class)
class DepartamentoServiceTest {

    private static final int SOFTWARE_INC = 1;
    private static final int SECURITY = 42;

    @Mock
    private DepartamentoRepository repository;

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private DepartamentoService service;

    private static DepartamentoDTO dto(String nombre, Integer idEmpresa) {
        return new DepartamentoDTO(null, nombre, idEmpresa, null);
    }

    private static Empresa empresa(int id, String nombre) {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(id);
        empresa.setNombre(nombre);
        return empresa;
    }

    private static Departamento existente(Integer id, String nombre, Integer idEmpresa) {
        LocalDateTime creacion = LocalDateTime.of(2026, 1, 15, 8, 30);
        return new Departamento(id, nombre, idEmpresa, "Software Inc.", creacion, "creador", null, null);
    }

    @Test
    void create_nombreDuplicadoEnLaMismaEmpresa_lanza409SinGuardar() {
        when(empresaRepository.findById(SOFTWARE_INC)).thenReturn(Optional.of(empresa(SOFTWARE_INC, "Software Inc.")));
        when(repository.existsByEmpresaYNombre(SOFTWARE_INC, "Finanzas")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto("Finanzas", SOFTWARE_INC)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).isEqualTo("Ya existe un departamento con ese nombre en la empresa seleccionada.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_mismoNombreEnOtraEmpresa_seGuardaConElNombreDeLaEmpresa() {
        when(empresaRepository.findById(SECURITY)).thenReturn(Optional.of(empresa(SECURITY, "Security")));
        when(repository.existsByEmpresaYNombre(SECURITY, "Finanzas")).thenReturn(false);
        when(repository.save(any(Departamento.class))).thenAnswer(inv -> {
            Departamento d = inv.getArgument(0);
            d.setIdDepartamento(30);
            return d;
        });

        DepartamentoDTO creado = service.create(dto("Finanzas", SECURITY));

        assertThat(creado.getIdDepartamento()).isEqualTo(30);
        assertThat(creado.getIdEmpresa()).isEqualTo(SECURITY);
        assertThat(creado.getNombreEmpresa()).isEqualTo("Security");
    }

    @Test
    void create_empresaInexistente_lanza400SinGuardar() {
        when(empresaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto("Legal", 99)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).isEqualTo("La empresa seleccionada no existe.");
                });
        verify(repository, never()).save(any());
    }

    @Test
    void create_recortaEspaciosYLlenaAuditoriaDeCreacion_ignorandoNombreEmpresaDeEntrada() {
        when(empresaRepository.findById(SOFTWARE_INC)).thenReturn(Optional.of(empresa(SOFTWARE_INC, "Software Inc.")));
        when(repository.existsByEmpresaYNombre(SOFTWARE_INC, "Auditoría Interna")).thenReturn(false);
        when(repository.save(any(Departamento.class))).thenAnswer(inv -> inv.getArgument(0));

        DepartamentoDTO entrada = new DepartamentoDTO(null, "  Auditoría Interna  ", SOFTWARE_INC, "nombre falso");
        service.create(entrada);

        ArgumentCaptor<Departamento> captor = ArgumentCaptor.forClass(Departamento.class);
        verify(repository).save(captor.capture());
        Departamento guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Auditoría Interna");
        assertThat(guardado.getIdEmpresa()).isEqualTo(SOFTWARE_INC);
        assertThat(guardado.getNombreEmpresa()).isNotEqualTo("nombre falso");
        assertThat(guardado.getFechaCreacion()).isNotNull();
        assertThat(guardado.getUsuarioCreacion()).isEqualTo("system"); // sin usuario autenticado
        assertThat(guardado.getFechaModificacion()).isNull();
    }

    @Test
    void update_nombreDeOtroDepartamentoDeLaMismaEmpresa_lanza409SinActualizar() {
        when(repository.findById(7)).thenReturn(Optional.of(existente(7, "Legal", SOFTWARE_INC)));
        when(empresaRepository.findById(SOFTWARE_INC)).thenReturn(Optional.of(empresa(SOFTWARE_INC, "Software Inc.")));
        when(repository.existsByEmpresaYNombreExcluyendoId(SOFTWARE_INC, "Compras", 7)).thenReturn(true);

        assertThatThrownBy(() -> service.update(7, dto("Compras", SOFTWARE_INC)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(repository, never()).update(any());
    }

    @Test
    void update_mismoNombreDelPropioRegistro_noEsDuplicadoYConservaCreacion() {
        Departamento actual = existente(7, "Legal", SOFTWARE_INC);
        when(repository.findById(7)).thenReturn(Optional.of(actual));
        when(empresaRepository.findById(SOFTWARE_INC)).thenReturn(Optional.of(empresa(SOFTWARE_INC, "Software Inc.")));
        when(repository.existsByEmpresaYNombreExcluyendoId(SOFTWARE_INC, "LEGAL", 7)).thenReturn(false);

        DepartamentoDTO actualizado = service.update(7, dto(" LEGAL ", SOFTWARE_INC));

        ArgumentCaptor<Departamento> captor = ArgumentCaptor.forClass(Departamento.class);
        verify(repository).update(captor.capture());
        Departamento enviado = captor.getValue();
        assertThat(enviado.getIdDepartamento()).isEqualTo(7);
        assertThat(enviado.getNombre()).isEqualTo("LEGAL");
        assertThat(enviado.getFechaCreacion()).isEqualTo(actual.getFechaCreacion());
        assertThat(enviado.getUsuarioCreacion()).isEqualTo("creador");
        assertThat(enviado.getFechaModificacion()).isNotNull();
        assertThat(enviado.getUsuarioModificacion()).isEqualTo("system");
        assertThat(actualizado.getNombreEmpresa()).isEqualTo("Software Inc.");
        // Misma empresa: no hace falta contar puestos.
        verify(repository, never()).countPuestos(anyInt());
    }

    @Test
    void update_cambiarDeEmpresaConPuestos_lanza409SinActualizar() {
        when(repository.findById(11)).thenReturn(Optional.of(existente(11, "Tecnologías de la Información (IT)", SOFTWARE_INC)));
        when(empresaRepository.findById(SECURITY)).thenReturn(Optional.of(empresa(SECURITY, "Security")));
        when(repository.countPuestos(11)).thenReturn(12);

        assertThatThrownBy(() -> service.update(11, dto("Tecnologías de la Información (IT)", SECURITY)))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason())
                            .isEqualTo("No se puede cambiar la empresa del departamento: tiene 12 puesto(s) asociado(s).");
                });
        verify(repository, never()).update(any());
    }

    @Test
    void update_cambiarDeEmpresaSinPuestos_sePermite() {
        when(repository.findById(7)).thenReturn(Optional.of(existente(7, "Legal", SOFTWARE_INC)));
        when(empresaRepository.findById(SECURITY)).thenReturn(Optional.of(empresa(SECURITY, "Security")));
        when(repository.countPuestos(7)).thenReturn(0);
        when(repository.existsByEmpresaYNombreExcluyendoId(SECURITY, "Legal", 7)).thenReturn(false);

        DepartamentoDTO actualizado = service.update(7, dto("Legal", SECURITY));

        assertThat(actualizado.getIdEmpresa()).isEqualTo(SECURITY);
        assertThat(actualizado.getNombreEmpresa()).isEqualTo("Security");
        verify(repository).update(any(Departamento.class));
    }

    @Test
    void update_idInexistente_lanza404() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, dto("Lo que sea", SOFTWARE_INC)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void delete_idInexistente_lanza404() {
        when(repository.deleteById(99)).thenReturn(0);

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).isEqualTo("Departamento no encontrado con id: 99");
                });
    }
}
