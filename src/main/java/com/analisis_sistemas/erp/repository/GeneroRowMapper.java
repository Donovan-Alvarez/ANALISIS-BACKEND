package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Genero;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class GeneroRowMapper implements RowMapper<Genero> {

    @Override
    public Genero mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genero genero = new Genero();

        genero.setIdGenero(rs.getObject("IDGENERO", Integer.class));
        genero.setNombre(rs.getString("NOMBRE"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        genero.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        genero.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        genero.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        genero.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return genero;
    }
}
