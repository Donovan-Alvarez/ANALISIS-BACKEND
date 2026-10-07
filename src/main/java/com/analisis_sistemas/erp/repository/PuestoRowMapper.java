package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Puesto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Espera las columnas del SELECT con JOIN de PuestoRepository (NOMBREDEPARTAMENTO, IDEMPRESA, NOMBREEMPRESA). */
@Component
public class PuestoRowMapper implements RowMapper<Puesto> {

    @Override
    public Puesto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Puesto puesto = new Puesto();

        puesto.setIdPuesto(rs.getObject("IDPUESTO", Integer.class));
        puesto.setNombre(rs.getString("NOMBRE"));
        puesto.setIdDepartamento(rs.getObject("IDDEPARTAMENTO", Integer.class));
        puesto.setNombreDepartamento(rs.getString("NOMBREDEPARTAMENTO"));
        puesto.setIdEmpresa(rs.getObject("IDEMPRESA", Integer.class));
        puesto.setNombreEmpresa(rs.getString("NOMBREEMPRESA"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        puesto.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        puesto.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        puesto.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        puesto.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return puesto;
    }
}
