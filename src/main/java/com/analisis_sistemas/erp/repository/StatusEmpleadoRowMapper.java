package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.StatusEmpleado;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class StatusEmpleadoRowMapper implements RowMapper<StatusEmpleado> {

    @Override
    public StatusEmpleado mapRow(ResultSet rs, int rowNum) throws SQLException {
        StatusEmpleado statusEmpleado = new StatusEmpleado();

        statusEmpleado.setIdStatusEmpleado(rs.getObject("IDSTATUSEMPLEADO", Integer.class));
        statusEmpleado.setNombre(rs.getString("NOMBRE"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        statusEmpleado.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        statusEmpleado.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        statusEmpleado.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        statusEmpleado.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return statusEmpleado;
    }
}
