package com.analisis_sistemas.erp.dto;
import com.analisis_sistemas.erp.entity.BoletaPago;
import java.util.List;
public record BoletaPagoDTO(BoletaPago detalle,List<CuentaPagoDTO> cuentasActivas) {
 public record CuentaPagoDTO(Integer idCuentaBancaria,String banco,String numeroDeCuenta){}
 public record PeriodoPagoDTO(Integer anio,Integer mes){}
 public record EmpleadoPagoDTO(Integer idEmpleado,String nombre){}
}
