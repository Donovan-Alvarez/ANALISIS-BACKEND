package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Sucursal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class SucursalRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SucursalRowMapper sucursalRowMapper;

    public SucursalRepository(JdbcTemplate jdbcTemplate, SucursalRowMapper sucursalRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.sucursalRowMapper = sucursalRowMapper;
    }

    public List<Sucursal> findAll() {
        String sql = """
                SELECT IdSucursal, Nombre, Direccion, IdEmpresa,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM SUCURSAL
                ORDER BY IdSucursal
                """;
        return jdbcTemplate.query(sql, sucursalRowMapper);
    }

    public Optional<Sucursal> findById(Integer id) {
        String sql = """
                SELECT IdSucursal, Nombre, Direccion, IdEmpresa,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM SUCURSAL
                WHERE IdSucursal = ?
                """;
        return jdbcTemplate.query(sql, sucursalRowMapper, id).stream().findFirst();
    }

    public List<Sucursal> findByEmpresaId(Integer idEmpresa) {
        String sql = """
                SELECT IdSucursal, Nombre, Direccion, IdEmpresa,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM SUCURSAL
                WHERE IdEmpresa = ?
                ORDER BY IdSucursal
                """;
        return jdbcTemplate.query(sql, sucursalRowMapper, idEmpresa);
    }

    public Sucursal save(Sucursal sucursal) {
        String sql = """
                INSERT INTO SUCURSAL (
                    Nombre, Direccion, IdEmpresa, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDSUCURSAL"});
            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getDireccion());
            ps.setInt(3, sucursal.getIdEmpresa());
            ps.setTimestamp(4, Timestamp.valueOf(sucursal.getFechaCreacion()));
            ps.setString(5, sucursal.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        sucursal.setIdSucursal(keyHolder.getKey().intValue());
        return sucursal;
    }

    public int update(Sucursal sucursal) {
        String sql = """
                UPDATE SUCURSAL
                SET Nombre = ?, Direccion = ?, IdEmpresa = ?,
                    FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdSucursal = ?
                """;

        return jdbcTemplate.update(sql,
                sucursal.getNombre(),
                sucursal.getDireccion(),
                sucursal.getIdEmpresa(),
                Timestamp.valueOf(sucursal.getFechaModificacion()),
                sucursal.getUsuarioModificacion(),
                sucursal.getIdSucursal());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM SUCURSAL WHERE IdSucursal = ?";
        return jdbcTemplate.update(sql, id);
    }
}
