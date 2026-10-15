package com.analisis_sistemas.erp.service;
import com.analisis_sistemas.erp.dto.BoletaPagoDTO;
import com.analisis_sistemas.erp.dto.BoletaPagoDTO.*;
import com.analisis_sistemas.erp.repository.BoletaPagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
@Transactional(readOnly=true)
public class BoletaPagoService {
 private final BoletaPagoRepository repo;
 public BoletaPagoService(BoletaPagoRepository r){repo=r;}
 public List<PeriodoPagoDTO> periodos(){return repo.periodos();}
 public List<EmpleadoPagoDTO> empleados(int a,int m){validar(a,m);return repo.empleados(a,m);}
 public BoletaPagoDTO obtener(int a,int m,int id){
  validar(a,m);if(id<1)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Empleado inválido.");
  var detalles=repo.detalles(a,m,id);
  if(detalles.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay boleta procesada para este empleado y período.");
  if(detalles.size()>1)throw new ResponseStatusException(HttpStatus.CONFLICT,"Hay varios detalles para el mismo empleado y período. Revise la planilla antes de emitir la boleta.");
  return new BoletaPagoDTO(detalles.getFirst(),repo.cuentas(id));
 }
 private void validar(int a,int m){if(a<1||a>9999||m<1||m>12)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Período inválido.");}
}
