package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Banco;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class BancoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BancoRowMapper bancoRowMapper;

    public BancoRepository(JdbcTemplate jdbcTemplate, BancoRowMapper bancoRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bancoRowMapper = bancoRowMapper;
    }

    public List<Banco> findAll() {
        String sql = """
                SELECT IdBanco, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM BANCO
                ORDER BY Nombre
                """;
        return jdbcTemplate.query(sql, bancoRowMapper);
    }

    public Optional<Banco> findById(Integer id) {
        String sql = """
                SELECT IdBanco, Nombre,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM BANCO
                WHERE IdBanco = ?
                """;
        return jdbcTemplate.query(sql, bancoRowMapper, id).stream().findFirst();
    }

    /** Duplicado sin distinguir mayusculas ni espacios de los extremos (alta). */
    public boolean existsByNombre(String nombre) {
        String sql = """
                SELECT COUNT(*)
                FROM BANCO
                WHERE UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, nombre);
        return total != null && total > 0;
    }

    /** Igual que {@link #existsByNombre(String)}, pero sin contar el propio registro (cambio). */
    public boolean existsByNombreExcluyendoId(String nombre, Integer idExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM BANCO
                WHERE UPPER(TRIM(Nombre)) = UPPER(TRIM(?))
                  AND IdBanco <> ?
                """;
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, nombre, idExcluir);
        return total != null && total > 0;
    }

    public Banco save(Banco banco) {
        String sql = """
                INSERT INTO BANCO (
                    Nombre, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDBANCO"});
            ps.setString(1, banco.getNombre());
            ps.setTimestamp(2, Timestamp.valueOf(banco.getFechaCreacion()));
            ps.setString(3, banco.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        banco.setIdBanco(keyHolder.getKey().intValue());
        return banco;
    }

    public int update(Banco banco) {
        String sql = """
                UPDATE BANCO
                SET Nombre = ?, FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdBanco = ?
                """;

        return jdbcTemplate.update(sql,
                banco.getNombre(),
                Timestamp.valueOf(banco.getFechaModificacion()),
                banco.getUsuarioModificacion(),
                banco.getIdBanco());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM BANCO WHERE IdBanco = ?";
        return jdbcTemplate.update(sql, id);
    }
}
