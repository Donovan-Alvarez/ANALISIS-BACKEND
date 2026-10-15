package com.analisis_sistemas.erp.repository;
import com.analisis_sistemas.erp.entity.FlujoStatusEmpleado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.*;
@Repository
public class FlujoStatusEmpleadoRepository {
 private final JdbcTemplate jdbc; private final FlujoStatusEmpleadoRowMapper mapper;
 private static final String BASE="SELECT f.*, a.Nombre STATUSACTUAL, n.Nombre STATUSNUEVO FROM FLUJO_STATUS_EMPLEADO f JOIN STATUS_EMPLEADO a ON a.IdStatusEmpleado=f.IdStatusActual JOIN STATUS_EMPLEADO n ON n.IdStatusEmpleado=f.IdStatusNuevo ";
 public FlujoStatusEmpleadoRepository(JdbcTemplate j,FlujoStatusEmpleadoRowMapper m){jdbc=j;mapper=m;}
 public List<FlujoStatusEmpleado> findAll(){return jdbc.query(BASE+"ORDER BY a.Nombre,n.Nombre",mapper);}
 public Optional<FlujoStatusEmpleado> findById(int a,int n){return jdbc.query(BASE+"WHERE f.IdStatusActual=? AND f.IdStatusNuevo=?",mapper,a,n).stream().findFirst();}
 public boolean statusExiste(int id){return jdbc.queryForObject("SELECT COUNT(*) FROM STATUS_EMPLEADO WHERE IdStatusEmpleado=?",Integer.class,id)>0;}
 public void save(FlujoStatusEmpleado e){jdbc.update("{call PR_FLUJO_INSERTAR(?,?,?,?,?)}",e.getIdStatusActual(),e.getIdStatusNuevo(),e.getNombreEvento(),Timestamp.valueOf(e.getFechaCreacion()),e.getUsuarioCreacion());}
 public void update(int a,int n,FlujoStatusEmpleado e){jdbc.update("{call PR_FLUJO_ACTUALIZAR(?,?,?,?,?,?,?)}",a,n,e.getIdStatusActual(),e.getIdStatusNuevo(),e.getNombreEvento(),Timestamp.valueOf(e.getFechaModificacion()),e.getUsuarioModificacion());}
 public void delete(int a,int n){jdbc.update("{call PR_FLUJO_ELIMINAR(?,?)}",a,n);}
}
