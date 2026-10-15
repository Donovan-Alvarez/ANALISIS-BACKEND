package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.dto.EmpleadoLiquidableDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Espera las columnas de FN_EMPLEADO_LIQUIDABLES. */
@Component
public class EmpleadoLiquidableRowMapper implements RowMapper<EmpleadoLiquidableDTO> {

    @Override
    public EmpleadoLiquidableDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmpleadoLiquidableDTO dto = new EmpleadoLiquidableDTO();

        dto.setIdEmpleado(rs.getObject("IDEMPLEADO", Integer.class));
        dto.setNombrePersona(rs.getString("NOMBREPERSONA"));
        dto.setIdPuesto(rs.getObject("IDPUESTO", Integer.class));
        dto.setNombrePuesto(rs.getString("NOMBREPUESTO"));

        Date fechaContratacion = rs.getDate("FECHACONTRATACION");
        dto.setFechaContratacion(fechaContratacion != null ? fechaContratacion.toLocalDate() : null);

        dto.setIngresoSueldoBase(rs.getBigDecimal("INGRESOSUELDOBASE"));
        dto.setIngresoBonificacionDecreto(rs.getBigDecimal("INGRESOBONIFICACIONDECRETO"));
        dto.setIngresoOtrosIngresos(rs.getBigDecimal("INGRESOOTROSINGRESOS"));
        dto.setDescuentoIgss(rs.getBigDecimal("DESCUENTOIGSS"));
        dto.setDescuentoIsr(rs.getBigDecimal("DESCUENTOISR"));
        dto.setDescuentoInasistencias(rs.getBigDecimal("DESCUENTOINASISTENCIAS"));

        return dto;
    }
}
