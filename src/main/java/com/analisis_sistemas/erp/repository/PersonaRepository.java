package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Persona;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonaRepository {

    /** Todas las lecturas traen el nombre del género y del estado civil. */
    private static final String SELECT_BASE = """
            SELECT p.IdPersona, p.Nombre, p.Apellido, p.FechaNacimiento,
                   p.IdGenero, g.Nombre AS NombreGenero,
                   p.Direccion, p.Telefono, p.CorreoElectronico,
                   p.IdEstadoCivil, ec.Nombre AS NombreEstadoCivil,
                   p.FechaCreacion, p.UsuarioCreacion, p.FechaModificacion, p.UsuarioModificacion
            FROM PERSONA p
            JOIN GENERO g ON g.IdGenero = p.IdGenero
            JOIN ESTADO_CIVIL ec ON ec.IdEstadoCivil = p.IdEstadoCivil
            """;

    private final JdbcTemplate jdbcTemplate;
    private final PersonaRowMapper personaRowMapper;

    public PersonaRepository(JdbcTemplate jdbcTemplate, PersonaRowMapper personaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.personaRowMapper = personaRowMapper;
    }

    public List<Persona> findAll() {
        String sql = SELECT_BASE + "ORDER BY p.Apellido, p.Nombre, p.IdPersona";
        return jdbcTemplate.query(sql, personaRowMapper);
    }

    public Optional<Persona> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE p.IdPersona = ?";
        return jdbcTemplate.query(sql, personaRowMapper, id).stream().findFirst();
    }

    public Persona save(Persona persona) {
        String sql = """
                INSERT INTO PERSONA (
                    Nombre, Apellido, FechaNacimiento, IdGenero, Direccion, Telefono,
                    CorreoElectronico, IdEstadoCivil, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDPERSONA"});
            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellido());
            ps.setDate(3, Date.valueOf(persona.getFechaNacimiento()));
            ps.setInt(4, persona.getIdGenero());
            ps.setString(5, persona.getDireccion());
            ps.setString(6, persona.getTelefono());
            if (persona.getCorreoElectronico() != null) {
                ps.setString(7, persona.getCorreoElectronico());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }
            ps.setInt(8, persona.getIdEstadoCivil());
            ps.setTimestamp(9, Timestamp.valueOf(persona.getFechaCreacion()));
            ps.setString(10, persona.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        persona.setIdPersona(keyHolder.getKey().intValue());
        return persona;
    }

    public int update(Persona persona) {
        String sql = """
                UPDATE PERSONA
                SET Nombre = ?, Apellido = ?, FechaNacimiento = ?, IdGenero = ?, Direccion = ?,
                    Telefono = ?, CorreoElectronico = ?, IdEstadoCivil = ?,
                    FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdPersona = ?
                """;

        return jdbcTemplate.update(sql,
                persona.getNombre(),
                persona.getApellido(),
                Date.valueOf(persona.getFechaNacimiento()),
                persona.getIdGenero(),
                persona.getDireccion(),
                persona.getTelefono(),
                persona.getCorreoElectronico(),
                persona.getIdEstadoCivil(),
                Timestamp.valueOf(persona.getFechaModificacion()),
                persona.getUsuarioModificacion(),
                persona.getIdPersona());
    }

    /**
     * Si la persona es un empleado o tiene documentos, TRG_PERSONA_BAJA_VALIDA
     * (ORA-20180/-20181) rechaza el DELETE y GlobalExceptionHandler responde 409
     * con el mensaje del trigger (D2).
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM PERSONA WHERE IdPersona = ?";
        return jdbcTemplate.update(sql, id);
    }
}
