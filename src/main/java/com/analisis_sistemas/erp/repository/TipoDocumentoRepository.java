package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.TipoDocumento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class TipoDocumentoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TipoDocumentoRowMapper tipoDocumentoRowMapper;

    public TipoDocumentoRepository(JdbcTemplate jdbcTemplate, TipoDocumentoRowMapper tipoDocumentoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tipoDocumentoRowMapper = tipoDocumentoRowMapper;
    }

    public List<TipoDocumento> findAll() {
        String sql = """
                SELECT IdTipoDocumento, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM TIPO_DOCUMENTO
                ORDER BY Nombre
                """;
        return jdbcTemplate.query(sql, tipoDocumentoRowMapper);
    }

    public Optional<TipoDocumento> findById(Integer id) {
        String sql = """
                SELECT IdTipoDocumento, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM TIPO_DOCUMENTO
                WHERE IdTipoDocumento = ?
                """;
        return jdbcTemplate.query(sql, tipoDocumentoRowMapper, id).stream().findFirst();
    }

    /** Duplicado sin distinguir mayusculas ni espacios de los extremos (alta). */
    public boolean existsByNombre(String nombre) {
        String sql = """
                SELECT COUNT(*)
                FROM TIPO_DOCUMENTO
                WHERE UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, nombre);
        return total != null && total > 0;
    }

    /** Igual que {@link #existsByNombre(String)}, pero sin contar el propio registro (cambio). */
    public boolean existsByNombreExcluyendoId(String nombre, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM TIPO_DOCUMENTO
                WHERE UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                  AND IdTipoDocumento <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, nombre, idExcluir);
        return total != null && total > 0;
    }

    public TipoDocumento save(TipoDocumento tipoDocumento) {
        String sql = """
                INSERT INTO TIPO_DOCUMENTO (
                    Nombre, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDTIPODOCUMENTO"});
            ps.setString(1, tipoDocumento.getNombre());
            ps.setTimestamp(2, Timestamp.valueOf(tipoDocumento.getFechaCreacion()));
            ps.setString(3, tipoDocumento.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        tipoDocumento.setIdTipoDocumento(keyHolder.getKey().intValue());
        return tipoDocumento;
    }

    public int update(TipoDocumento tipoDocumento) {
        String sql = """
                UPDATE TIPO_DOCUMENTO
                SET Nombre = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdTipoDocumento = ?
                """;

        return jdbcTemplate.update(sql,
                tipoDocumento.getNombre(),
                Timestamp.valueOf(tipoDocumento.getFechaModificacion()),
                tipoDocumento.getUsuarioModificacion(),
                tipoDocumento.getIdTipoDocumento());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM TIPO_DOCUMENTO WHERE IdTipoDocumento = ?";
        return jdbcTemplate.update(sql, id);
    }
}
