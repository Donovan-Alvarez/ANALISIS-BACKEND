package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Opcion;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class OpcionRowMapper implements RowMapper<Opcion> {

    @Override
    public Opcion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Opcion opcion = new Opcion();

        opcion.setIdOpcion(rs.getObject("IDOPCION", Integer.class));
        opcion.setIdMenu(rs.getObject("IDMENU", Integer.class));
        opcion.setNombre(rs.getString("NOMBRE"));
        opcion.setOrdenMenu(rs.getObject("ORDENMENU", Integer.class));
        opcion.setPagina(rs.getString("PAGINA"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        opcion.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        opcion.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        opcion.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        opcion.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return opcion;
    }
}
