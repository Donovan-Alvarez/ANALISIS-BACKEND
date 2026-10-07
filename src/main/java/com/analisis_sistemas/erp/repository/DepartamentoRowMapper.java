package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Departamento;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Espera las columnas del SELECT con JOIN de DepartamentoRepository (incluida NOMBREEMPRESA). */
@Component
public class DepartamentoRowMapper implements RowMapper<Departamento> {

    @Override
    public Departamento mapRow(ResultSet rs, int rowNum) throws SQLException {
        Departamento departamento = new Departamento();

        departamento.setIdDepartamento(rs.getObject("IDDEPARTAMENTO", Integer.class));
        departamento.setNombre(rs.getString("NOMBRE"));
        departamento.setIdEmpresa(rs.getObject("IDEMPRESA", Integer.class));
        departamento.setNombreEmpresa(rs.getString("NOMBREEMPRESA"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        departamento.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        departamento.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        departamento.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        departamento.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return departamento;
    }
}
