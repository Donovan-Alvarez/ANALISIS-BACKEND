package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.TipoAcceso;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TipoAccesoRowMapper implements RowMapper<TipoAcceso> {

    @Override
    public TipoAcceso mapRow(ResultSet rs, int rowNum) throws SQLException {
        TipoAcceso tipoAcceso = new TipoAcceso();
        tipoAcceso.setIdTipoAcceso(rs.getObject("IDTIPOACCESO", Integer.class));
        tipoAcceso.setNombre(rs.getString("NOMBRE"));
        return tipoAcceso;
    }
}
