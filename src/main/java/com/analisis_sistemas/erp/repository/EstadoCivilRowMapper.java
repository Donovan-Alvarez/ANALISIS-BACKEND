package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.EstadoCivil;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class EstadoCivilRowMapper implements RowMapper<EstadoCivil> {

    @Override
    public EstadoCivil mapRow(ResultSet rs, int rowNum) throws SQLException {
        EstadoCivil estadoCivil = new EstadoCivil();

        estadoCivil.setIdEstadoCivil(rs.getObject("IDESTADOCIVIL", Integer.class));
        estadoCivil.setNombre(rs.getString("NOMBRE"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        estadoCivil.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        estadoCivil.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        estadoCivil.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        estadoCivil.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return estadoCivil;
    }
}
