package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Empresa;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class EmpresaRowMapper implements RowMapper<Empresa> {

    @Override
    public Empresa mapRow(ResultSet rs, int rowNum) throws SQLException {
        Empresa empresa = new Empresa();

        empresa.setIdEmpresa(rs.getObject("IDEMPRESA", Integer.class));
        empresa.setNombre(rs.getString("NOMBRE"));
        empresa.setDireccion(rs.getString("DIRECCION"));
        empresa.setNit(rs.getString("NIT"));
        empresa.setPasswordCantidadMayusculas(rs.getObject("PASSWORDCANTIDADMAYUSCULAS", Integer.class));
        empresa.setPasswordCantidadMinusculas(rs.getObject("PASSWORDCANTIDADMINUSCULAS", Integer.class));
        empresa.setPasswordCantidadCaracteresEspeciales(rs.getObject("PASSWORDCANTIDADCARACTERESESPECIALES", Integer.class));
        empresa.setPasswordCantidadCaducidadDias(rs.getObject("PASSWORDCANTIDADCADUCIDADDIAS", Integer.class));
        empresa.setPasswordLargo(rs.getObject("PASSWORDLARGO", Integer.class));
        empresa.setPasswordIntentosAntesDeBloquear(rs.getObject("PASSWORDINTENTOSANTESDEBLOQUEAR", Integer.class));
        empresa.setPasswordCantidadNumeros(rs.getObject("PASSWORDCANTIDADNUMEROS", Integer.class));
        empresa.setPasswordCantidadPreguntasValidar(rs.getObject("PASSWORDCANTIDADPREGUNTASVALIDAR", Integer.class));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        empresa.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        empresa.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        empresa.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        empresa.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return empresa;
    }
}
