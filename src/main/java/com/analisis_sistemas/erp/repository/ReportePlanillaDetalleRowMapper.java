package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.ReportePlanillaDetalle;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReportePlanillaDetalleRowMapper implements RowMapper<ReportePlanillaDetalle> {

    @Override
    public ReportePlanillaDetalle mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReportePlanillaDetalle detalle = new ReportePlanillaDetalle();

        detalle.setIdPlanillaDetalle(rs.getObject("IDPLANILLADETALLE", Integer.class));
        detalle.setIdEmpleado(rs.getObject("IDEMPLEADO", Integer.class));
        detalle.setNombreEmpleado(rs.getString("NOMBREEMPLEADO"));
        detalle.setNombrePuesto(rs.getString("NOMBREPUESTO"));
        detalle.setNombreDepartamento(rs.getString("NOMBREDEPARTAMENTO"));
        detalle.setIngresoSueldoBase(rs.getBigDecimal("INGRESOSUELDOBASE"));
        detalle.setIngresoBonificacionDecreto(rs.getBigDecimal("INGRESOBONIFICACIONDECRETO"));
        detalle.setIngresoOtrosIngresos(rs.getBigDecimal("INGRESOOTROSINGRESOS"));
        detalle.setTotalIngresos(rs.getBigDecimal("TOTALINGRESOS"));
        detalle.setDescuentoIgss(rs.getBigDecimal("DESCUENTOIGSS"));
        detalle.setDescuentoIsr(rs.getBigDecimal("DESCUENTOISR"));
        detalle.setDescuentoInasistencias(rs.getBigDecimal("DESCUENTOINASISTENCIAS"));
        detalle.setTotalDescuentos(rs.getBigDecimal("TOTALDESCUENTOS"));
        detalle.setSalarioNeto(rs.getBigDecimal("SALARIONETO"));
        detalle.setDiasInasistencia(rs.getObject("DIASINASISTENCIA", Integer.class));

        return detalle;
    }
}
