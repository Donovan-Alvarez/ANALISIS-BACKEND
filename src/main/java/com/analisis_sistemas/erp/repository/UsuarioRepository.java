package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRowMapper usuarioRowMapper;

    public UsuarioRepository(JdbcTemplate jdbcTemplate, UsuarioRowMapper usuarioRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRowMapper = usuarioRowMapper;
    }

    public List<Usuario> findAll() {
        String sql = """
                SELECT IdUsuario, Nombre, Apellido, FechaNacimiento, IdStatusUsuario, Password, IdGenero,
                       UltimaFechaIngreso, IntentosDeAcceso, SesionActual, UltimaFechaCambioPassword,
                       CorreoElectronico, RequiereCambiarPassword, Fotografia, TelefonoMovil, IdSucursal,
                       Pregunta, Respuesta, IdRole,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM USUARIO
                ORDER BY IdUsuario
                """;
        return jdbcTemplate.query(sql, usuarioRowMapper);
    }

    public Optional<Usuario> findById(String id) {
        String sql = """
                SELECT IdUsuario, Nombre, Apellido, FechaNacimiento, IdStatusUsuario, Password, IdGenero,
                       UltimaFechaIngreso, IntentosDeAcceso, SesionActual, UltimaFechaCambioPassword,
                       CorreoElectronico, RequiereCambiarPassword, Fotografia, TelefonoMovil, IdSucursal,
                       Pregunta, Respuesta, IdRole,
                       FechaCreacion, UsuarioCreacion, FechaModificacion, UsuarioModificacion
                FROM USUARIO
                WHERE IdUsuario = ?
                """;
        return jdbcTemplate.query(sql, usuarioRowMapper, id).stream().findFirst();
    }

    public Usuario save(Usuario usuario) {
        String sql = """
                INSERT INTO USUARIO (
                    IdUsuario, Nombre, Apellido, FechaNacimiento, IdStatusUsuario, Password, IdGenero,
                    UltimaFechaIngreso, IntentosDeAcceso, SesionActual, UltimaFechaCambioPassword,
                    CorreoElectronico, RequiereCambiarPassword, Fotografia, TelefonoMovil, IdSucursal,
                    Pregunta, Respuesta, IdRole, FechaCreacion, UsuarioCreacion
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                Date.valueOf(usuario.getFechaNacimiento()),
                usuario.getIdStatusUsuario(),
                usuario.getPassword(),
                usuario.getIdGenero(),
                toTimestamp(usuario.getUltimaFechaIngreso()),
                usuario.getIntentosDeAcceso(),
                usuario.getSesionActual(),
                toTimestamp(usuario.getUltimaFechaCambioPassword()),
                usuario.getCorreoElectronico(),
                toIntFlag(usuario.getRequiereCambiarPassword()),
                usuario.getFotografia(),
                usuario.getTelefonoMovil(),
                usuario.getIdSucursal(),
                usuario.getPregunta(),
                usuario.getRespuesta(),
                usuario.getIdRole(),
                Timestamp.valueOf(usuario.getFechaCreacion()),
                usuario.getUsuarioCreacion());

        return usuario;
    }

    public int update(Usuario usuario) {
        String sql = """
                UPDATE USUARIO
                SET Nombre = ?, Apellido = ?, FechaNacimiento = ?, IdStatusUsuario = ?, Password = ?, IdGenero = ?,
                    UltimaFechaIngreso = ?, IntentosDeAcceso = ?, SesionActual = ?, UltimaFechaCambioPassword = ?,
                    CorreoElectronico = ?, RequiereCambiarPassword = ?, Fotografia = ?, TelefonoMovil = ?,
                    IdSucursal = ?, Pregunta = ?, Respuesta = ?, IdRole = ?,
                    FechaModificacion = ?, UsuarioModificacion = ?
                WHERE IdUsuario = ?
                """;

        return jdbcTemplate.update(sql,
                usuario.getNombre(),
                usuario.getApellido(),
                Date.valueOf(usuario.getFechaNacimiento()),
                usuario.getIdStatusUsuario(),
                usuario.getPassword(),
                usuario.getIdGenero(),
                toTimestamp(usuario.getUltimaFechaIngreso()),
                usuario.getIntentosDeAcceso(),
                usuario.getSesionActual(),
                toTimestamp(usuario.getUltimaFechaCambioPassword()),
                usuario.getCorreoElectronico(),
                toIntFlag(usuario.getRequiereCambiarPassword()),
                usuario.getFotografia(),
                usuario.getTelefonoMovil(),
                usuario.getIdSucursal(),
                usuario.getPregunta(),
                usuario.getRespuesta(),
                usuario.getIdRole(),
                toTimestamp(usuario.getFechaModificacion()),
                usuario.getUsuarioModificacion(),
                usuario.getIdUsuario());
    }

    public int deleteById(String id) {
        String sql = "DELETE FROM USUARIO WHERE IdUsuario = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void registrarIntentoFallido(String idUsuario, Integer nuevosIntentos, Integer idStatusUsuario) {
        String sql = "UPDATE USUARIO SET IntentosDeAcceso = ?, IdStatusUsuario = ? WHERE IdUsuario = ?";
        jdbcTemplate.update(sql, nuevosIntentos, idStatusUsuario, idUsuario);
    }

    public void registrarLoginExitoso(String idUsuario) {
        String sql = "UPDATE USUARIO SET IntentosDeAcceso = 0, UltimaFechaIngreso = SYSTIMESTAMP WHERE IdUsuario = ?";
        jdbcTemplate.update(sql, idUsuario);
    }

    public void actualizarPassword(String idUsuario, String passwordHash, LocalDateTime ultimaFechaCambioPassword) {
        String sql = "UPDATE USUARIO SET Password = ?, UltimaFechaCambioPassword = ? WHERE IdUsuario = ?";
        jdbcTemplate.update(sql, passwordHash, toTimestamp(ultimaFechaCambioPassword), idUsuario);
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }

    private Integer toIntFlag(Boolean value) {
        return value != null ? (value ? 1 : 0) : null;
    }
}
