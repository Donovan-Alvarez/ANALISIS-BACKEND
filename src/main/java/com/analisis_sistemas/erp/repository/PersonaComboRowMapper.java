package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.dto.PersonaComboDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Espera las columnas de FN_PERSONA_COMBO. */
@Component
public class PersonaComboRowMapper implements RowMapper<PersonaComboDTO> {

    @Override
    public PersonaComboDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        PersonaComboDTO dto = new PersonaComboDTO();
        dto.setIdPersona(rs.getObject("IDPERSONA", Integer.class));
        dto.setNombreCompleto(rs.getString("NOMBRECOMPLETO"));
        return dto;
    }
}
