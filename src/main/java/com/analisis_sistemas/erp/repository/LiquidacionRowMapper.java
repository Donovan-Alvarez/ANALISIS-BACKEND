package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Liquidacion;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Espera las columnas de FN_LIQUIDACION_LISTAR y FN_LIQUIDACION_POR_ID. */
@Component
public class LiquidacionRowMapper implements RowMapper<Liquidacion> {

    @Override
    public Liquidacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Liquidacion liquidacion = new Liquidacion();

        liquidacion.setIdLiquidacion(rs.getObject("IDLIQUIDACION", Integer.class));
        liquidacion.setIdEmpleado(rs.getObject("IDEMPLEADO", Integer.class));
        liquidacion.setIdPuesto(rs.getObject("IDPUESTO", Integer.class));

        liquidacion.setFechaContratacion(aLocalDate(rs.getDate("FECHACONTRATACION")));
        liquidacion.setFechaEgreso(aLocalDate(rs.getDate("FECHAEGRESO")));
        liquidacion.setFechaLiquidacion(aLocalDate(rs.getDate("FECHALIQUIDACION")));
        liquidacion.setMotivoEgreso(rs.getString("MOTIVOEGRESO"));

        liquidacion.setIngresoSueldoBase(rs.getBigDecimal("INGRESOSUELDOBASE"));
        liquidacion.setIngresoBonificacionDecreto(rs.getBigDecimal("INGRESOBONIFICACIONDECRETO"));
        liquidacion.setIngresoOtrosIngresos(rs.getBigDecimal("INGRESOOTROSINGRESOS"));
        liquidacion.setDescuentoIgss(rs.getBigDecimal("DESCUENTOIGSS"));
        liquidacion.setDescuentoIsr(rs.getBigDecimal("DESCUENTOISR"));
        liquidacion.setDescuentoInasistencias(rs.getBigDecimal("DESCUENTOINASISTENCIAS"));

        liquidacion.setSalarioNeto(rs.getBigDecimal("SALARIONETO"));
        liquidacion.setTotalIngresos(rs.getBigDecimal("TOTALINGRESOS"));
        liquidacion.setTotalDescuentos(rs.getBigDecimal("TOTALDESCUENTOS"));
        liquidacion.setTotalNeto(rs.getBigDecimal("TOTALNETO"));

        liquidacion.setNombrePersona(rs.getString("NOMBREPERSONA"));
        liquidacion.setNombrePuesto(rs.getString("NOMBREPUESTO"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        liquidacion.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);
        liquidacion.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        return liquidacion;
    }

    private java.time.LocalDate aLocalDate(Date fecha) {
        return fecha != null ? fecha.toLocalDate() : null;
    }
}
