package com.analisis_sistemas.erp.repository;
import com.analisis_sistemas.erp.dto.BoletaPagoDTO.*;
import com.analisis_sistemas.erp.entity.BoletaPago;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class BoletaPagoRepository {
 private final JdbcTemplate jdbc;private final BoletaPagoRowMapper mapper;
 public BoletaPagoRepository(JdbcTemplate j,BoletaPagoRowMapper m){jdbc=j;mapper=m;}
 public List<PeriodoPagoDTO> periodos(){return jdbc.query("SELECT c.Anio,c.Mes FROM PLANILLA_CABECERA c WHERE c.FechaHoraProcesada IS NOT NULL ORDER BY c.Anio DESC,c.Mes DESC",(r,n)->new PeriodoPagoDTO(r.getInt("ANIO"),r.getInt("MES")));}
 public List<EmpleadoPagoDTO> empleados(int a,int m){return jdbc.query("SELECT DISTINCT d.IdEmpleado,p.Nombre || ' ' || p.Apellido NOMBRE FROM PLANILLA_DETALLE d JOIN PLANILLA_CABECERA c ON c.Anio=d.Anio AND c.Mes=d.Mes JOIN EMPLEADO e ON e.IdEmpleado=d.IdEmpleado JOIN PERSONA p ON p.IdPersona=e.IdPersona WHERE d.Anio=? AND d.Mes=? AND c.FechaHoraProcesada IS NOT NULL ORDER BY NOMBRE",(r,n)->new EmpleadoPagoDTO(r.getInt("IDEMPLEADO"),r.getString("NOMBRE")),a,m);}
 public List<BoletaPago> detalles(int a,int m,int id){return jdbc.query("SELECT d.*,p.Nombre || ' ' || p.Apellido EMPLEADO,pu.Nombre PUESTO FROM PLANILLA_DETALLE d JOIN PLANILLA_CABECERA c ON c.Anio=d.Anio AND c.Mes=d.Mes JOIN EMPLEADO e ON e.IdEmpleado=d.IdEmpleado JOIN PERSONA p ON p.IdPersona=e.IdPersona JOIN PUESTO pu ON pu.IdPuesto=d.IdPuesto WHERE d.Anio=? AND d.Mes=? AND d.IdEmpleado=? AND c.FechaHoraProcesada IS NOT NULL",mapper,a,m,id);}
 // Se consultan aparte para que varias cuentas activas no multipliquen el detalle salarial.
 public List<CuentaPagoDTO> cuentas(int id){return jdbc.query("SELECT cb.IdCuentaBancaria,b.Nombre BANCO,cb.NumeroDeCuenta FROM CUENTA_BANCARIA_EMPLEADO cb JOIN BANCO b ON b.IdBanco=cb.IdBanco WHERE cb.IdEmpleado=? AND cb.Activa=1 ORDER BY b.Nombre,cb.IdCuentaBancaria",(r,n)->new CuentaPagoDTO(r.getInt("IDCUENTABANCARIA"),r.getString("BANCO"),r.getString("NUMERODECUENTA")),id);}
}
