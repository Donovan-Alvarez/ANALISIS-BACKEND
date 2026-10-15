package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Persona;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Espera las columnas del SELECT con JOIN de PersonaRepository (NOMBREGENERO, NOMBREESTADOCIVIL). */
@Component
public class PersonaRowMapper implements RowMapper<Persona> {

    @Override
    public Persona mapRow(ResultSet rs, int rowNum) throws SQLException {
        Persona persona = new Persona();

        persona.setIdPersona(rs.getObject("IDPERSONA", Integer.class));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setApellido(rs.getString("APELLIDO"));

        Date fechaNacimiento = rs.getDate("FECHANACIMIENTO");
        persona.setFechaNacimiento(fechaNacimiento != null ? fechaNacimiento.toLocalDate() : null);

        persona.setIdGenero(rs.getObject("IDGENERO", Integer.class));
        persona.setNombreGenero(rs.getString("NOMBREGENERO"));
        persona.setDireccion(rs.getString("DIRECCION"));
        persona.setTelefono(rs.getString("TELEFONO"));
        persona.setCorreoElectronico(rs.getString("CORREOELECTRONICO"));
        persona.setIdEstadoCivil(rs.getObject("IDESTADOCIVIL", Integer.class));
        persona.setNombreEstadoCivil(rs.getString("NOMBREESTADOCIVIL"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        persona.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);
        persona.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        persona.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);
        persona.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return persona;
    }
}
