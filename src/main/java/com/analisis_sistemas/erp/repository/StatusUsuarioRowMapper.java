package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.StatusUsuario;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class StatusUsuarioRowMapper implements RowMapper<StatusUsuario> {

    @Override
    public StatusUsuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        StatusUsuario statusUsuario = new StatusUsuario();

        statusUsuario.setIdStatusUsuario(rs.getObject("IDSTATUSUSUARIO", Integer.class));
        statusUsuario.setNombre(rs.getString("NOMBRE"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        statusUsuario.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        statusUsuario.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        statusUsuario.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        statusUsuario.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return statusUsuario;
    }
}
