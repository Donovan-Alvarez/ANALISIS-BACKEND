package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Modulo;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class ModuloRowMapper implements RowMapper<Modulo> {

    @Override
    public Modulo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Modulo modulo = new Modulo();

        modulo.setIdModulo(rs.getObject("IDMODULO", Integer.class));
        modulo.setNombre(rs.getString("NOMBRE"));
        modulo.setOrdenMenu(rs.getObject("ORDENMENU", Integer.class));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        modulo.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        modulo.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        modulo.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        modulo.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return modulo;
    }
}
