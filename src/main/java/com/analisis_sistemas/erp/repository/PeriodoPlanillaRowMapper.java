package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.PeriodoPlanilla;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class PeriodoPlanillaRowMapper implements RowMapper<PeriodoPlanilla> {

    @Override
    public PeriodoPlanilla mapRow(ResultSet rs, int rowNum) throws SQLException {
        PeriodoPlanilla periodo = new PeriodoPlanilla();

        periodo.setAnio(rs.getObject("ANIO", Integer.class));
        periodo.setMes(rs.getObject("MES", Integer.class));

        Date fechaInicio = rs.getDate("FECHAINICIO");
        periodo.setFechaInicio(fechaInicio != null ? fechaInicio.toLocalDate() : null);

        Date fechaFin = rs.getDate("FECHAFIN");
        periodo.setFechaFin(fechaFin != null ? fechaFin.toLocalDate() : null);

        periodo.setTotalIngresos(rs.getBigDecimal("TOTALINGRESOS"));
        periodo.setTotalDescuentos(rs.getBigDecimal("TOTALDESCUENTOS"));
        periodo.setSalarioNeto(rs.getBigDecimal("SALARIONETO"));

        Timestamp procesada = rs.getTimestamp("FECHAHORAPROCESADA");
        periodo.setFechaHoraProcesada(procesada != null ? procesada.toLocalDateTime() : null);

        periodo.setUsuarioProceso(rs.getString("USUARIOPROCESO"));
        periodo.setEmpleadosCalculados(rs.getObject("EMPLEADOSCALCULADOS", Integer.class));

        return periodo;
    }
}