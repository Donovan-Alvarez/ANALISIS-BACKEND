package com.analisis_sistemas.erp.service;

import com.analisis_sistemas.erp.dto.ReportePlanillaDTO;
import com.analisis_sistemas.erp.entity.ReportePlanillaCabecera;
import com.analisis_sistemas.erp.entity.ReportePlanillaDetalle;
import com.analisis_sistemas.erp.repository.ReportePlanillaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** El reporte solo suma lo almacenado y compara contra la cabecera guardada, al centavo. */
@ExtendWith(MockitoExtension.class)
class ReportePlanillaServiceTest {

    @Mock
    private ReportePlanillaRepository repository;

    @InjectMocks
    private ReportePlanillaService service;

    private static BigDecimal q(String valor) {
        return new BigDecimal(valor);
    }

    private static ReportePlanillaCabecera cabecera(String ingresos, String descuentos, String neto) {
        ReportePlanillaCabecera c = new ReportePlanillaCabecera();
        c.setAnio(2026);
        c.setMes(9);
        c.setTotalIngresos(q(ingresos));
        c.setTotalDescuentos(q(descuentos));
        c.setSalarioNeto(q(neto));
        c.setUsuarioProceso("Administrador");
        c.setEmpleados(2);
        return c;
    }

    /** Valores con centavos que en double no suman exacto (0.1 + 0.2). */
    private static ReportePlanillaDetalle fila(int id, String sueldo, String igss, String inasist, String neto) {
        ReportePlanillaDetalle d = new ReportePlanillaDetalle();
        d.setIdPlanillaDetalle(id);
        d.setIdEmpleado(id);
        d.setIngresoSueldoBase(q(sueldo));
        d.setIngresoBonificacionDecreto(q("250.00"));
        d.setIngresoOtrosIngresos(q("0.10"));
        d.setTotalIngresos(q(sueldo).add(q("250.10")));
        d.setDescuentoIgss(q(igss));
        d.setDescuentoIsr(q("0.20"));
        d.setDescuentoInasistencias(q(inasist));
        d.setTotalDescuentos(q(igss).add(q("0.20")).add(q(inasist)));
        d.setSalarioNeto(q(neto));
        d.setDiasInasistencia(inasist.equals("0.00") ? 0 : 2);
        return d;
    }

    @Test
    void obtener_sumaLasFilasAlmacenadasYCuadraConLaCabecera() {
        when(repository.findCabecera(2026, 9)).thenReturn(Optional.of(cabecera("8500.53", "586.84", "7913.69")));
        when(repository.findDetalle(2026, 9)).thenReturn(List.of(
                fila(1, "5000.00", "241.50", "0.00", "5008.40"),
                fila(2, "3000.33", "144.92", "200.02", "2905.29")));

        ReportePlanillaDTO reporte = service.obtener(2026, 9);

        assertThat(reporte.totales().empleados()).isEqualTo(2);
        assertThat(reporte.totales().totalIngresos()).isEqualByComparingTo("8500.53");
        assertThat(reporte.totales().totalDescuentos()).isEqualByComparingTo("586.84");
        assertThat(reporte.totales().salarioNeto()).isEqualByComparingTo("7913.69");
        assertThat(reporte.totales().ingresoOtrosIngresos()).isEqualByComparingTo("0.20");
        assertThat(reporte.totales().descuentoIsr()).isEqualByComparingTo("0.40");
        assertThat(reporte.totales().diasInasistencia()).isEqualTo(2);
        assertThat(reporte.cuadraConCabecera()).isTrue();
        assertThat(reporte.detalle()).hasSize(2);
        assertThat(reporte.usuarioProceso()).isEqualTo("Administrador");
    }

    @Test
    void obtener_noRecalculaElNetoAlmacenadoYAvisaSiNoCuadra() {
        when(repository.findCabecera(2026, 9)).thenReturn(Optional.of(cabecera("5250.10", "241.70", "5008.40")));
        when(repository.findDetalle(2026, 9)).thenReturn(List.of(fila(1, "5000.00", "241.50", "0.00", "9999.99")));

        ReportePlanillaDTO reporte = service.obtener(2026, 9);

        assertThat(reporte.detalle().getFirst().salarioNeto()).isEqualByComparingTo("9999.99");
        assertThat(reporte.cuadraConCabecera()).isFalse();
        assertThat(reporte.cabecera().salarioNeto()).isEqualByComparingTo("5008.40");
    }

    @Test
    void obtener_escalasDistintasDelMismoMontoCuadran() {
        when(repository.findCabecera(2026, 9)).thenReturn(Optional.of(cabecera("5250.1", "241.7", "5008.4")));
        when(repository.findDetalle(2026, 9)).thenReturn(List.of(fila(1, "5000.00", "241.50", "0.00", "5008.40")));

        assertThat(service.obtener(2026, 9).cuadraConCabecera()).isTrue();
    }

    @Test
    void obtener_periodoSinCalcular_lanza404() {
        when(repository.findCabecera(2026, 11)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(2026, 11))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).isEqualTo("La planilla del período 11/2026 todavía no ha sido calculada.");
                });
    }

    @Test
    void obtener_periodoInvalido_lanza400SinConsultar() {
        assertThatThrownBy(() -> service.obtener(2026, 13))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verifyNoInteractions(repository);
    }
}
