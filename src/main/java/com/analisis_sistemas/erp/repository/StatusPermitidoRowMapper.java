package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.dto.StatusPermitidoDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Espera las columnas de FN_EMPLEADO_STATUS_PERMITIDOS. */
@Component
public class StatusPermitidoRowMapper implements RowMapper<StatusPermitidoDTO> {

    @Override
    public StatusPermitidoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        StatusPermitidoDTO dto = new StatusPermitidoDTO();
        dto.setIdStatusEmpleado(rs.getObject("IDSTATUSEMPLEADO", Integer.class));
        dto.setNombre(rs.getString("NOMBRE"));
        dto.setNombreEvento(rs.getString("NOMBREEVENTO"));
        return dto;
    }
}
