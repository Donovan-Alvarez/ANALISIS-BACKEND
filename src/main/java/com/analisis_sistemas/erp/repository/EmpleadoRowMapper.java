package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Empleado;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Espera las columnas que devuelven FN_EMPLEADO_LISTAR y FN_EMPLEADO_POR_ID
 * (ver database/07_fase2_sp_empleado_liquidacion.sql), incluidas las de los
 * JOIN: NOMBREPERSONA, NOMBRESUCURSAL, NOMBREPUESTO, NOMBREDEPARTAMENTO y
 * NOMBRESTATUS.
 */
@Component
public class EmpleadoRowMapper implements RowMapper<Empleado> {

    @Override
    public Empleado mapRow(ResultSet rs, int rowNum) throws SQLException {
        Empleado empleado = new Empleado();

        empleado.setIdEmpleado(rs.getObject("IDEMPLEADO", Integer.class));
        empleado.setIdPersona(rs.getObject("IDPERSONA", Integer.class));
        empleado.setIdSucursal(rs.getObject("IDSUCURSAL", Integer.class));
        empleado.setIdPuesto(rs.getObject("IDPUESTO", Integer.class));
        empleado.setIdStatusEmpleado(rs.getObject("IDSTATUSEMPLEADO", Integer.class));

        Date fechaContratacion = rs.getDate("FECHACONTRATACION");
        empleado.setFechaContratacion(fechaContratacion != null ? fechaContratacion.toLocalDate() : null);

        empleado.setIngresoSueldoBase(rs.getBigDecimal("INGRESOSUELDOBASE"));
        empleado.setIngresoBonificacionDecreto(rs.getBigDecimal("INGRESOBONIFICACIONDECRETO"));
        empleado.setIngresoOtrosIngresos(rs.getBigDecimal("INGRESOOTROSINGRESOS"));
        empleado.setDescuentoIgss(rs.getBigDecimal("DESCUENTOIGSS"));
        empleado.setDescuentoIsr(rs.getBigDecimal("DESCUENTOISR"));
        empleado.setDescuentoInasistencias(rs.getBigDecimal("DESCUENTOINASISTENCIAS"));

        empleado.setNombrePersona(rs.getString("NOMBREPERSONA"));
        empleado.setNombreSucursal(rs.getString("NOMBRESUCURSAL"));
        empleado.setNombrePuesto(rs.getString("NOMBREPUESTO"));
        empleado.setIdDepartamento(rs.getObject("IDDEPARTAMENTO", Integer.class));
        empleado.setNombreDepartamento(rs.getString("NOMBREDEPARTAMENTO"));
        empleado.setNombreStatus(rs.getString("NOMBRESTATUS"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        empleado.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        empleado.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        empleado.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        empleado.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return empleado;
    }
}
