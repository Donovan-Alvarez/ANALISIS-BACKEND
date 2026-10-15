package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.DocumentoPersona;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Espera las columnas del SELECT con JOIN de DocumentoPersonaRepository (NOMBRETIPODOCUMENTO, NOMBREPERSONA). */
@Component
public class DocumentoPersonaRowMapper implements RowMapper<DocumentoPersona> {

    @Override
    public DocumentoPersona mapRow(ResultSet rs, int rowNum) throws SQLException {
        DocumentoPersona documento = new DocumentoPersona();

        documento.setIdTipoDocumento(rs.getObject("IDTIPODOCUMENTO", Integer.class));
        documento.setIdPersona(rs.getObject("IDPERSONA", Integer.class));
        documento.setNoDocumento(rs.getString("NODOCUMENTO"));
        documento.setNombreTipoDocumento(rs.getString("NOMBRETIPODOCUMENTO"));
        documento.setNombrePersona(rs.getString("NOMBREPERSONA"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        documento.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);
        documento.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        documento.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);
        documento.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return documento;
    }
}
