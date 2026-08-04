package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.entity.Menu;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class MenuRowMapper implements RowMapper<Menu> {

    @Override
    public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
        Menu menu = new Menu();

        menu.setIdMenu(rs.getObject("IDMENU", Integer.class));
        menu.setIdModulo(rs.getObject("IDMODULO", Integer.class));
        menu.setNombre(rs.getString("NOMBRE"));
        menu.setOrdenMenu(rs.getObject("ORDENMENU", Integer.class));

        Timestamp fechaCreacion = rs.getTimestamp("FECHACREACION");
        menu.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        menu.setUsuarioCreacion(rs.getString("USUARIOCREACION"));

        Timestamp fechaModificacion = rs.getTimestamp("FECHAMODIFICACION");
        menu.setFechaModificacion(fechaModificacion != null ? fechaModificacion.toLocalDateTime() : null);

        menu.setUsuarioModificacion(rs.getString("USUARIOMODIFICACION"));

        return menu;
    }
}
