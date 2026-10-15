package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.entity.BoletaPago;
import com.analisis_sistemas.erp.repository.BoletaPagoRepository;
import com.analisis_sistemas.erp.dto.BoletaPagoDTO.CuentaPagoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoletaPagoServiceTest {
 private final BoletaPagoRepository repo=mock(BoletaPagoRepository.class);
 private final BoletaPagoService service=new BoletaPagoService(repo);
 @Test void conservaNetoAlmacenadoAunqueNoCoincidaConSumaActual(){
  var d=new BoletaPago();d.setSalarioNeto(new BigDecimal("1234.56"));d.setIngresoSueldoBase(new BigDecimal("9000.00"));
  when(repo.detalles(2026,10,1)).thenReturn(List.of(d));
  when(repo.cuentas(1)).thenReturn(List.of(new CuentaPagoDTO(1,"Banco A","001"),new CuentaPagoDTO(2,"Banco B","002")));
  var b=service.obtener(2026,10,1);
  assertThat(b.detalle().getSalarioNeto()).isEqualByComparingTo("1234.56");assertThat(b.cuentasActivas()).hasSize(2);
 }
 @Test void rechazaDetalleAmbiguoSinEscogerUnaFilaArbitraria(){
  when(repo.detalles(2026,10,1)).thenReturn(List.of(new BoletaPago(),new BoletaPago()));
  assertThatThrownBy(()->service.obtener(2026,10,1)).isInstanceOfSatisfying(ResponseStatusException.class,e->assertThat(e.getStatusCode().value()).isEqualTo(409));
 }
 @Test void rechazaPeriodoInvalidoAntesDeConsultar(){
  assertThatThrownBy(()->service.obtener(2026,13,1)).isInstanceOf(ResponseStatusException.class);verifyNoInteractions(repo);
 }
 @Test void sinPlanillaProcesadaDevuelve404(){
  when(repo.detalles(2026,10,1)).thenReturn(List.of());
  assertThatThrownBy(()->service.obtener(2026,10,1)).isInstanceOfSatisfying(ResponseStatusException.class,e->assertThat(e.getStatusCode().value()).isEqualTo(404));
 }
}
