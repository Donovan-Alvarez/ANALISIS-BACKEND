package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Usuario;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class UsuarioRowMapper implements RowMapper<Usuario> {

    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setIdUsuario(rs.getString("IDUSUARIO"));
        usuario.setNombre(rs.getString("NOMBRE"));
        usuario.setApellido(rs.getString("APELLIDO"));

        Date fechaNacimiento = rs.getDate("FECHANACIMIENTO");
        usuario.setFechaNacimiento(fechaNacimiento != null ? fechaNacimiento.toLocalDate() : null);

        usuario.setIdStatusUsuario(rs.getObject("IDSTATUSUSUARIO", Integer.class));
        usuario.setPassword(rs.getString("PASSWORD"));
        usuario.setIdGenero(rs.getObject("IDGENERO", Integer.class));

        Timestamp ultimaFechaIngreso = rs.getTimestamp("ULTIMAFECHAINGRESO");
        usuario.setUltimaFechaIngreso(ultimaFechaIngreso != null ? ultimaFechaIngreso.toLocalDateTime() : null);

        usuario.setIntentosDeAcceso(rs.getObject("INTENTOSDEACCESO", Integer.class));
        usuario.setSesionActual(rs.getString("SESIONACTUAL"));

        Timestamp ultimaFechaCambioPassword = rs.getTimestamp("ULTIMAFECHACAMBIOPASSWORD");
        usuario.setUltimaFechaCambioPassword(ultimaFechaCambioPassword != null ? ultimaFechaCambioPassword.toLocalDateTime() : null);

        usuario.setCorreoElectronico(rs.getString("CORREOELECTRONICO"));

        Integer requiereCambiarPassword = rs.getObject("REQUIERECAMBIARPASSWORD", Integer.class);
        usuario.setRequiereCambiarPassword(requiereCambiarPassword != null ? requiereCambiarPassword == 1 : null);

        usuario.setFotografia(rs.getBytes("FOTOGRAFIA"));
        usuario.setTelefonoMovil(rs.getString("TELEFONOMOVIL"));
        usuario.setIdSucursal(rs.getObject("IDSUCURSAL", Integer.class));
        usuario.setPregunta(rs.getString("PREGUNTA"));
        usuario.setRespuesta(rs.getString("RESPUESTA"));
        usuario.setIdRole(rs.getObject("IDROLE", Integer.class));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        usuario.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        usuario.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        usuario.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        usuario.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return usuario;
    }
}
