package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Sucursal;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class SucursalRowMapper implements RowMapper<Sucursal> {

    @Override
    public Sucursal mapRow(ResultSet rs, int rowNum) throws SQLException {
        Sucursal sucursal = new Sucursal();

        sucursal.setIdSucursal(rs.getObject("IDSUCURSAL", Integer.class));
        sucursal.setNombre(rs.getString("NOMBRE"));
        sucursal.setDireccion(rs.getString("DIRECCION"));
        sucursal.setIdEmpresa(rs.getObject("IDEMPRESA", Integer.class));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        sucursal.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        sucursal.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        sucursal.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        sucursal.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return sucursal;
    }
}
