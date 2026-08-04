package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Role;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();

        role.setIdRole(rs.getObject("IDROLE", Integer.class));
        role.setNombre(rs.getString("NOMBRE"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        role.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        role.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        role.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        role.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return role;
    }
}
