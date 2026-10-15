package com.analisis_sistemas.erp.service;
import com.analisis_sistemas.erp.dto.FlujoStatusEmpleadoDTO;
import com.analisis_sistemas.erp.entity.FlujoStatusEmpleado;
import com.analisis_sistemas.erp.repository.FlujoStatusEmpleadoRepository;
import com.analisis_sistemas.erp.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;
@Service
@Transactional
public class FlujoStatusEmpleadoService {
 private final FlujoStatusEmpleadoRepository repo;
 public FlujoStatusEmpleadoService(FlujoStatusEmpleadoRepository r){repo=r;}
 public List<FlujoStatusEmpleadoDTO> findAll(){return repo.findAll().stream().map(this::dto).toList();}
 public FlujoStatusEmpleadoDTO findById(int a,int n){return dto(buscar(a,n));}
 private FlujoStatusEmpleado buscar(int a,int n){return repo.findById(a,n).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"La transición no existe."));}
 private void validar(FlujoStatusEmpleadoDTO d){
  if(Objects.equals(d.getIdStatusActual(),d.getIdStatusNuevo())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El status actual y el nuevo deben ser distintos.");
  if(!repo.statusExiste(d.getIdStatusActual())||!repo.statusExiste(d.getIdStatusNuevo())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El status seleccionado no existe.");
 }
 private void duplicado(){throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya existe una transición entre esos status.");}
 public FlujoStatusEmpleadoDTO create(FlujoStatusEmpleadoDTO d){
  validar(d); if(repo.findById(d.getIdStatusActual(),d.getIdStatusNuevo()).isPresent()) duplicado();
  var e=entity(d); e.setFechaCreacion(LocalDateTime.now());e.setUsuarioCreacion(SecurityUtils.getUsuarioAutenticado()); repo.save(e);return findById(e.getIdStatusActual(),e.getIdStatusNuevo());
 }
 public FlujoStatusEmpleadoDTO update(int a,int n,FlujoStatusEmpleadoDTO d){
  var old=buscar(a,n); validar(d);
  if((a!=d.getIdStatusActual()||n!=d.getIdStatusNuevo())&&repo.findById(d.getIdStatusActual(),d.getIdStatusNuevo()).isPresent()) duplicado();
  var e=entity(d);e.setFechaCreacion(old.getFechaCreacion());e.setUsuarioCreacion(old.getUsuarioCreacion());e.setFechaModificacion(LocalDateTime.now());e.setUsuarioModificacion(SecurityUtils.getUsuarioAutenticado());repo.update(a,n,e);return findById(e.getIdStatusActual(),e.getIdStatusNuevo());
 }
 public void delete(int a,int n){buscar(a,n);repo.delete(a,n);}
 private FlujoStatusEmpleado entity(FlujoStatusEmpleadoDTO d){var e=new FlujoStatusEmpleado();e.setIdStatusActual(d.getIdStatusActual());e.setIdStatusNuevo(d.getIdStatusNuevo());e.setNombreEvento(d.getNombreEvento().trim());return e;}
 private FlujoStatusEmpleadoDTO dto(FlujoStatusEmpleado e){var d=new FlujoStatusEmpleadoDTO();d.setIdStatusActual(e.getIdStatusActual());d.setIdStatusNuevo(e.getIdStatusNuevo());d.setNombreEvento(e.getNombreEvento());d.setStatusActual(e.getStatusActual());d.setStatusNuevo(e.getStatusNuevo());return d;}
}
