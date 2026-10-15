package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Inasistencia;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


@Component
public class InasistenciaRowMapper implements RowMapper<Inasistencia> {

    @Override
    public Inasistencia mapRow(ResultSet rs, int rowNum) throws SQLException {
        Inasistencia inasistencia = new Inasistencia();

        inasistencia.setIdInasistencia(rs.getObject("IDINASISTENCIA", Integer.class));
        inasistencia.setIdEmpleado(rs.getObject("IDEMPLEADO", Integer.class));
        inasistencia.setNombreEmpleado(rs.getString("NOMBREEMPLEADO"));
        inasistencia.setNombrePuesto(rs.getString("NOMBREPUESTO"));
        inasistencia.setNombreDepartamento(rs.getString("NOMBREDEPARTAMENTO"));

        Date fechaInicial = rs.getDate("FECHAINICIAL");
        inasistencia.setFechaInicial(fechaInicial != null ? fechaInicial.toLocalDate() : null);

        Date fechaFinal = rs.getDate("FECHAFINAL");
        inasistencia.setFechaFinal(fechaFinal != null ? fechaFinal.toLocalDate() : null);

        inasistencia.setMotivoInasistencia(rs.getString("MOTIVOINASISTENCIA"));

        Timestamp fechaProcesado = rs.getTimestamp("FECHAPROCESADO");
        inasistencia.setFechaProcesado(fechaProcesado != null ? fechaProcesado.toLocalDateTime() : null);

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        inasistencia.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        inasistencia.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        inasistencia.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        inasistencia.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return inasistencia;
    }
}