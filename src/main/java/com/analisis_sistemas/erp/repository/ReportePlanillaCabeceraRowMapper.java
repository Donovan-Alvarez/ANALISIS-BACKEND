package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.ReportePlanillaCabecera;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class ReportePlanillaCabeceraRowMapper implements RowMapper<ReportePlanillaCabecera> {

    @Override
    public ReportePlanillaCabecera mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReportePlanillaCabecera cabecera = new ReportePlanillaCabecera();

        cabecera.setAnio(rs.getObject("ANIO", Integer.class));
        cabecera.setMes(rs.getObject("MES", Integer.class));

        Date fechaInicio = rs.getDate("FECHAINICIO");
        cabecera.setFechaInicio(fechaInicio != null ? fechaInicio.toLocalDate() : null);

        Date fechaFin = rs.getDate("FECHAFIN");
        cabecera.setFechaFin(fechaFin != null ? fechaFin.toLocalDate() : null);

        cabecera.setTotalIngresos(rs.getBigDecimal("TOTALINGRESOS"));
        cabecera.setTotalDescuentos(rs.getBigDecimal("TOTALDESCUENTOS"));
        cabecera.setSalarioNeto(rs.getBigDecimal("SALARIONETO"));

        Timestamp procesada = rs.getTimestamp("FECHAHORAPROCESADA");
        cabecera.setFechaHoraProcesada(procesada != null ? procesada.toLocalDateTime() : null);

        cabecera.setUsuarioProceso(rs.getString("USUARIOPROCESO"));
        cabecera.setEmpleados(rs.getObject("EMPLEADOS", Integer.class));

        return cabecera;
    }
}
