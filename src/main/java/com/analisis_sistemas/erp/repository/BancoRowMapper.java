package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Banco;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class BancoRowMapper implements RowMapper<Banco> {

    @Override
    public Banco mapRow(ResultSet rs, int rowNum) throws SQLException {
        Banco banco = new Banco();

        banco.setIdBanco(rs.getObject("IDBANCO", Integer.class));
        banco.setNombre(rs.getString("NOMBRE"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        banco.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        banco.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        banco.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        banco.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return banco;
    }
}
