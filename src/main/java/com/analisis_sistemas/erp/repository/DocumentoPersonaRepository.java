package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.DocumentoPersona;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class DocumentoPersonaRepository {

    /** Todas las lecturas traen el nombre del tipo de documento y el nombre completo de la persona. */
    private static final String SELECT_BASE = """
            SELECT dp.IdTipoDocumento, dp.IdPersona, dp.NoDocumento,
                   td.Nombre AS NombreTipoDocumento,
                   p.Nombre || ' ' || p.Apellido AS NombrePersona,
                   dp.FechaCreacion, dp.UsuarioCreacion, dp.FechaModificacion, dp.UsuarioModificacion
            FROM DOCUMENTO_PERSONA dp
            JOIN TIPO_DOCUMENTO td ON td.IdTipoDocumento = dp.IdTipoDocumento
            JOIN PERSONA p ON p.IdPersona = dp.IdPersona
            """;

    private final JdbcTemplate jdbcTemplate;
    private final DocumentoPersonaRowMapper documentoPersonaRowMapper;

    public DocumentoPersonaRepository(JdbcTemplate jdbcTemplate, DocumentoPersonaRowMapper documentoPersonaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.documentoPersonaRowMapper = documentoPersonaRowMapper;
    }

    public List<DocumentoPersona> findAll() {
        String sql = SELECT_BASE + "ORDER BY p.Apellido, p.Nombre, p.IdPersona, td.Nombre";
        return jdbcTemplate.query(sql, documentoPersonaRowMapper);
    }

    public List<DocumentoPersona> findByPersonaId(Integer idPersona) {
        String sql = SELECT_BASE + "WHERE dp.IdPersona = ? ORDER BY td.Nombre";
        return jdbcTemplate.query(sql, documentoPersonaRowMapper, idPersona);
    }

    public Optional<DocumentoPersona> findById(Integer idTipoDocumento, Integer idPersona) {
        String sql = SELECT_BASE + "WHERE dp.IdTipoDocumento = ? AND dp.IdPersona = ?";
        return jdbcTemplate.query(sql, documentoPersonaRowMapper, idTipoDocumento, idPersona).stream().findFirst();
    }

    /** Ya existe ese tipo de documento para esa persona (la PK compuesta lo garantiza en BD; esto da el mensaje bonito). */
    public boolean existsByPersonaYTipo(Integer idPersona, Integer idTipoDocumento) {
        String sql = "SELECT COUNT(*) FROM DOCUMENTO_PERSONA WHERE IdPersona = ? AND IdTipoDocumento = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idPersona, idTipoDocumento);
        return total != null && total > 0;
    }

    public DocumentoPersona save(DocumentoPersona documento) {
        String sql = """
                INSERT INTO DOCUMENTO_PERSONA (
                    IdTipoDocumento, IdPersona, NoDocumento, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                documento.getIdTipoDocumento(),
                documento.getIdPersona(),
                documento.getNoDocumento(),
                Timestamp.valueOf(documento.getFechaCreacion()),
                documento.getUsuarioCreacion());
        return documento;
    }

    /** Solo cambia el número: tipo y persona son la llave y no se modifican. */
    public int update(DocumentoPersona documento) {
        String sql = """
                UPDATE DOCUMENTO_PERSONA
                SET NoDocumento = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdTipoDocumento = ? AND IdPersona = ?
                """;

        return jdbcTemplate.update(sql,
                documento.getNoDocumento(),
                Timestamp.valueOf(documento.getFechaModificacion()),
                documento.getUsuarioModificacion(),
                documento.getIdTipoDocumento(),
                documento.getIdPersona());
    }

    public int deleteById(Integer idTipoDocumento, Integer idPersona) {
        String sql = "DELETE FROM DOCUMENTO_PERSONA WHERE IdTipoDocumento = ? AND IdPersona = ?";
        return jdbcTemplate.update(sql, idTipoDocumento, idPersona);
    }
}
