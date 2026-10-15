package com.analisis_sistemas.erp.repository;
import com.analisis_sistemas.erp.entity.BoletaPago;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.*;
@Component
public class BoletaPagoRowMapper implements RowMapper<BoletaPago> {
 public BoletaPago mapRow(ResultSet r,int n)throws SQLException{
  var e=new BoletaPago();e.setIdPlanillaDetalle(r.getInt("IDPLANILLADETALLE"));e.setAnio(r.getInt("ANIO"));e.setMes(r.getInt("MES"));e.setIdEmpleado(r.getInt("IDEMPLEADO"));e.setEmpleado(r.getString("EMPLEADO"));e.setPuesto(r.getString("PUESTO"));
  e.setFechaContratacion(r.getDate("FECHACONTRATACION").toLocalDate());
  e.setIngresoSueldoBase(r.getBigDecimal("INGRESOSUELDOBASE"));e.setIngresoBonificacionDecreto(r.getBigDecimal("INGRESOBONIFICACIONDECRETO"));e.setIngresoOtrosIngresos(r.getBigDecimal("INGRESOOTROSINGRESOS"));e.setDescuentoIgss(r.getBigDecimal("DESCUENTOIGSS"));e.setDescuentoIsr(r.getBigDecimal("DESCUENTOISR"));e.setDescuentoInasistencias(r.getBigDecimal("DESCUENTOINASISTENCIAS"));e.setSalarioNeto(r.getBigDecimal("SALARIONETO"));return e;
 }
}
