package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.TipoDocumento;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class TipoDocumentoRowMapper implements RowMapper<TipoDocumento> {

    @Override
    public TipoDocumento mapRow(ResultSet rs, int rowNum) throws SQLException {
        TipoDocumento tipoDocumento = new TipoDocumento();

        tipoDocumento.setIdTipoDocumento(rs.getObject("IDTIPODOCUMENTO", Integer.class));
        tipoDocumento.setNombre(rs.getString("NOMBRE"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        tipoDocumento.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        tipoDocumento.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        tipoDocumento.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        tipoDocumento.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return tipoDocumento;
    }
}
