package com.analisis_sistemas.erp.controller;
import com.analisis_sistemas.erp.dto.BoletaPagoDTO;
import com.analisis_sistemas.erp.dto.BoletaPagoDTO.*;
import com.analisis_sistemas.erp.service.BoletaPagoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/boletas-pago")
public class BoletaPagoController {
 private final BoletaPagoService service;
 public BoletaPagoController(BoletaPagoService s){service=s;}
 @GetMapping("/periodos") public List<PeriodoPagoDTO> periodos(){return service.periodos();}
 @GetMapping("/{anio}/{mes}/empleados") public List<EmpleadoPagoDTO> empleados(@PathVariable int anio,@PathVariable int mes){return service.empleados(anio,mes);}
 @GetMapping("/{anio}/{mes}/{idEmpleado}") public BoletaPagoDTO obtener(@PathVariable int anio,@PathVariable int mes,@PathVariable int idEmpleado){return service.obtener(anio,mes,idEmpleado);}
}
