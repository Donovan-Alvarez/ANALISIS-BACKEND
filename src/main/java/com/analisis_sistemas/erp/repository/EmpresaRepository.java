package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Empresa;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpresaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EmpresaRowMapper empresaRowMapper;

    public EmpresaRepository(JdbcTemplate jdbcTemplate, EmpresaRowMapper empresaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.empresaRowMapper = empresaRowMapper;
    }

    public List<Empresa> findAll() {
        String sql = """
                SELECT IdEmpresa, Nombre, Direccion, Nit,
                       PasswordCantidadMayusculas, PasswordCantidadMinusculas,
                       PasswordCantidadCaracteresEspeciales, PasswordCantidadCaducidadDias,
                       PasswordLargo, PasswordIntentosAntesDeBloquear,
                       PasswordCantidadNumeros, PasswordCantidadPreguntasValidar,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM EMPRESA
                ORDER BY IdEmpresa
                """;
        return jdbcTemplate.query(sql, empresaRowMapper);
    }

    public Optional<Empresa> findById(Integer id) {
        String sql = """
                SELECT IdEmpresa, Nombre, Direccion, Nit,
                       PasswordCantidadMayusculas, PasswordCantidadMinusculas,
                       PasswordCantidadCaracteresEspeciales, PasswordCantidadCaducidadDias,
                       PasswordLargo, PasswordIntentosAntesDeBloquear,
                       PasswordCantidadNumeros, PasswordCantidadPreguntasValidar,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM EMPRESA
                WHERE IdEmpresa = ?
                """;
        return jdbcTemplate.query(sql, empresaRowMapper, id).stream().findFirst();
    }

    public Empresa save(Empresa empresa) {
        String sql = """
                INSERT INTO EMPRESA (
                    Nombre, Direccion, Nit,
                    PasswordCantidadMayusculas, PasswordCantidadMinusculas,
                    PasswordCantidadCaracteresEspeciales, PasswordCantidadCaducidadDias,
                    PasswordLargo, PasswordIntentosAntesDeBloquear,
                    PasswordCantidadNumeros, PasswordCantidadPreguntasValidar,
                    FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"IDEMPRESA"});
            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getDireccion());
            ps.setString(3, empresa.getNit());
            ps.setObject(4, empresa.getPasswordCantidadMayusculas(), Types.INTEGER);
            ps.setObject(5, empresa.getPasswordCantidadMinusculas(), Types.INTEGER);
            ps.setObject(6, empresa.getPasswordCantidadCaracteresEspeciales(), Types.INTEGER);
            ps.setObject(7, empresa.getPasswordCantidadCaducidadDias(), Types.INTEGER);
            ps.setObject(8, empresa.getPasswordLargo(), Types.INTEGER);
            ps.setObject(9, empresa.getPasswordIntentosAntesDeBloquear(), Types.INTEGER);
            ps.setObject(10, empresa.getPasswordCantidadNumeros(), Types.INTEGER);
            ps.setObject(11, empresa.getPasswordCantidadPreguntasValidar(), Types.INTEGER);
            ps.setTimestamp(12, Timestamp.valueOf(empresa.getFechaCreacion()));
            ps.setString(13, empresa.getUsuarioCreacion());
            return ps;
        }, keyHolder);

        empresa.setIdEmpresa(keyHolder.getKey().intValue());
        return empresa;
    }

    public int update(Empresa empresa) {
        String sql = """
                UPDATE EMPRESA
                SET Nombre = ?, Direccion = ?, Nit = ?,
                    PasswordCantidadMayusculas = ?, PasswordCantidadMinusculas = ?,
                    PasswordCantidadCaracteresEspeciales = ?, PasswordCantidadCaducidadDias = ?,
                    PasswordLargo = ?, PasswordIntentosAntesDeBloquear = ?,
                    PasswordCantidadNumeros = ?, PasswordCantidadPreguntasValidar = ?,
                    FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdEmpresa = ?
                """;

        return jdbcTemplate.update(sql,
                empresa.getNombre(),
                empresa.getDireccion(),
                empresa.getNit(),
                empresa.getPasswordCantidadMayusculas(),
                empresa.getPasswordCantidadMinusculas(),
                empresa.getPasswordCantidadCaracteresEspeciales(),
                empresa.getPasswordCantidadCaducidadDias(),
                empresa.getPasswordLargo(),
                empresa.getPasswordIntentosAntesDeBloquear(),
                empresa.getPasswordCantidadNumeros(),
                empresa.getPasswordCantidadPreguntasValidar(),
                Timestamp.valueOf(empresa.getFechaModificacion()),
                empresa.getUsuarioModificacion(),
                empresa.getIdEmpresa());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM EMPRESA WHERE IdEmpresa = ?";
        return jdbcTemplate.update(sql, id);
    }
}
