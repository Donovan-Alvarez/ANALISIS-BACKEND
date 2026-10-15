package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.FlujoStatusEmpleadoDTO;
import com.analisis_sistemas.erp.entity.FlujoStatusEmpleado;
import com.analisis_sistemas.erp.repository.FlujoStatusEmpleadoRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Optional;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlujoStatusEmpleadoServiceTest {
 private final FlujoStatusEmpleadoRepository repo=mock(FlujoStatusEmpleadoRepository.class);
 private final FlujoStatusEmpleadoService service=new FlujoStatusEmpleadoService(repo);
 private FlujoStatusEmpleadoDTO dto(int a,int n){var d=new FlujoStatusEmpleadoDTO();d.setIdStatusActual(a);d.setIdStatusNuevo(n);d.setNombreEvento(" Evento ");return d;}
 @Test void rechazaTransicionAlMismoStatus(){
  assertThatThrownBy(()->service.create(dto(1,1))).isInstanceOfSatisfying(ResponseStatusException.class,e->assertThat(e.getStatusCode().value()).isEqualTo(400));
  verify(repo,never()).save(any());
 }
 @Test void rechazaDuplicadoAntesDeEscribir(){
  when(repo.statusExiste(anyInt())).thenReturn(true);
  when(repo.findById(1,2)).thenReturn(Optional.of(new FlujoStatusEmpleado()));
  assertThatThrownBy(()->service.create(dto(1,2))).isInstanceOfSatisfying(ResponseStatusException.class,e->assertThat(e.getStatusCode().value()).isEqualTo(409));
  verify(repo,never()).save(any());
 }
 @Test void cambiarClaveConservaAuditoriaDeCreacion(){
  var old=new FlujoStatusEmpleado();old.setFechaCreacion(LocalDateTime.of(2026,1,1,8,0));old.setUsuarioCreacion("creador");
  when(repo.findById(1,2)).thenReturn(Optional.of(old));when(repo.statusExiste(anyInt())).thenReturn(true);
  when(repo.findById(1,3)).thenReturn(Optional.empty(),Optional.of(old));
  service.update(1,2,dto(1,3));
  var captor=ArgumentCaptor.forClass(FlujoStatusEmpleado.class);verify(repo).update(eq(1),eq(2),captor.capture());
  assertThat(captor.getValue().getFechaCreacion()).isEqualTo(old.getFechaCreacion());
  assertThat(captor.getValue().getUsuarioCreacion()).isEqualTo("creador");
  assertThat(captor.getValue().getFechaModificacion()).isNotNull();
  assertThat(captor.getValue().getNombreEvento()).isEqualTo("Evento");
 }
}
