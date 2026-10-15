package com.analisis_sistemas.erp.repository;
import com.analisis_sistemas.erp.entity.FlujoStatusEmpleado;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.*;
@Component
public class FlujoStatusEmpleadoRowMapper implements RowMapper<FlujoStatusEmpleado> {
 public FlujoStatusEmpleado mapRow(ResultSet r,int n) throws SQLException {
  var e=new FlujoStatusEmpleado();
  e.setIdStatusActual(r.getInt("IDSTATUSACTUAL")); e.setIdStatusNuevo(r.getInt("IDSTATUSNUEVO"));
  e.setNombreEvento(r.getString("NOMBREEVENTO")); e.setStatusActual(r.getString("STATUSACTUAL")); e.setStatusNuevo(r.getString("STATUSNUEVO"));
  var c=r.getTimestamp("FECHACREACION"); e.setFechaCreacion(c==null?null:c.toLocalDateTime());
  var m=r.getTimestamp("FECHAMODIFICACION"); e.setFechaModificacion(m==null?null:m.toLocalDateTime());
  e.setUsuarioCreacion(r.getString("USUARIOCREACION")); e.setUsuarioModificacion(r.getString("USUARIOMODIFICACION")); return e;
 }
}
