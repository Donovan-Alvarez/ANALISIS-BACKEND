package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.CuentaBancariaEmpleado;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class CuentaBancariaEmpleadoRowMapper implements RowMapper<CuentaBancariaEmpleado> {

    @Override
    public CuentaBancariaEmpleado mapRow(ResultSet rs, int rowNum) throws SQLException {
        CuentaBancariaEmpleado cuenta = new CuentaBancariaEmpleado();

        cuenta.setIdCuentaBancaria(rs.getObject("IDCUENTABANCARIA", Integer.class));
        cuenta.setIdEmpleado(rs.getObject("IDEMPLEADO", Integer.class));
        cuenta.setIdBanco(rs.getObject("IDBANCO", Integer.class));
        cuenta.setNumeroDeCuenta(rs.getString("NUMERODECUENTA"));
        cuenta.setActiva(rs.getInt("ACTIVA") == 1);

        cuenta.setNombreEmpleado(rs.getString("NOMBREEMPLEADO"));
        cuenta.setNombrePuesto(rs.getString("NOMBREPUESTO"));
        cuenta.setNombreBanco(rs.getString("NOMBREBANCO"));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        cuenta.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        cuenta.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        cuenta.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        cuenta.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return cuenta;
    }
}
