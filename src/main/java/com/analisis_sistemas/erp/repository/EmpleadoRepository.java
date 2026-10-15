package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.dto.PersonaComboDTO;
import com.analisis_sistemas.erp.dto.StatusPermitidoDTO;
import com.analisis_sistemas.erp.entity.Empleado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Acceso a EMPLEADO exclusivamente por procedimientos almacenados, como pide
 * el estandar de Fase II: escritura con PROCEDURE, lectura con FUNCTION que
 * devuelve SYS_REFCURSOR. No hay una sola sentencia SQL en esta clase.
 * <p>
 * Los objetos los crea database/07_fase2_sp_empleado_liquidacion.sql.
 * <p>
 * Los parametros se declaran explicitos y en MAYUSCULAS, igual que en
 * TokenRecuperacionRepository y SesionRepository: Oracle guarda asi los
 * nombres y dejar que SimpleJdbcCall los deduzca por metadata falla en
 * silencio.
 */
@Repository
public class EmpleadoRepository {

    private static final String RETORNO = "RETURN_VALUE";

    private final SimpleJdbcCall callListar;
    private final SimpleJdbcCall callPorId;
    private final SimpleJdbcCall callStatusPermitidos;
    private final SimpleJdbcCall callTransicionValida;
    private final SimpleJdbcCall callInsertar;
    private final SimpleJdbcCall callActualizar;
    private final SimpleJdbcCall callEliminar;
    private final SimpleJdbcCall callPersonasCombo;

    public EmpleadoRepository(JdbcTemplate jdbcTemplate,
                               EmpleadoRowMapper empleadoRowMapper,
                               StatusPermitidoRowMapper statusPermitidoRowMapper,
                               PersonaComboRowMapper personaComboRowMapper) {

        this.callListar = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_EMPLEADO_LISTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter(RETORNO, Types.REF_CURSOR, empleadoRowMapper));

        this.callPorId = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_EMPLEADO_POR_ID")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter(RETORNO, Types.REF_CURSOR, empleadoRowMapper),
                        new SqlParameter("P_IDEMPLEADO", Types.NUMERIC));

        this.callStatusPermitidos = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_EMPLEADO_STATUS_PERMITIDOS")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter(RETORNO, Types.REF_CURSOR, statusPermitidoRowMapper),
                        new SqlParameter("P_IDEMPLEADO", Types.NUMERIC));

        this.callTransicionValida = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_EMPLEADO_TRANSICION_VALIDA")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter(RETORNO, Types.NUMERIC),
                        new SqlParameter("P_IDEMPLEADO", Types.NUMERIC),
                        new SqlParameter("P_IDSTATUSNUEVO", Types.NUMERIC));

        this.callInsertar = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PR_EMPLEADO_INSERTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_IDPERSONA", Types.NUMERIC),
                        new SqlParameter("P_IDSUCURSAL", Types.NUMERIC),
                        new SqlParameter("P_FECHACONTRATACION", Types.DATE),
                        new SqlParameter("P_IDPUESTO", Types.NUMERIC),
                        new SqlParameter("P_IDSTATUSEMPLEADO", Types.NUMERIC),
                        new SqlParameter("P_INGRESOSUELDOBASE", Types.NUMERIC),
                        new SqlParameter("P_INGRESOBONIFICACIONDECRETO", Types.NUMERIC),
                        new SqlParameter("P_INGRESOOTROSINGRESOS", Types.NUMERIC),
                        new SqlParameter("P_DESCUENTOIGSS", Types.NUMERIC),
                        new SqlParameter("P_DESCUENTOISR", Types.NUMERIC),
                        new SqlParameter("P_DESCUENTOINASISTENCIAS", Types.NUMERIC),
                        // Auditoria como TIMESTAMP: con DATE se perdia la hora
                        // y todas las altas quedaban a medianoche (D1).
                        new SqlParameter("P_FECHACREACION", Types.TIMESTAMP),
                        new SqlParameter("P_USUARIOCREACION", Types.VARCHAR),
                        new SqlOutParameter("P_IDEMPLEADO", Types.NUMERIC));

        this.callActualizar = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PR_EMPLEADO_ACTUALIZAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_IDEMPLEADO", Types.NUMERIC),
                        new SqlParameter("P_IDPERSONA", Types.NUMERIC),
                        new SqlParameter("P_IDSUCURSAL", Types.NUMERIC),
                        new SqlParameter("P_FECHACONTRATACION", Types.DATE),
                        new SqlParameter("P_IDPUESTO", Types.NUMERIC),
                        new SqlParameter("P_IDSTATUSEMPLEADO", Types.NUMERIC),
                        new SqlParameter("P_INGRESOSUELDOBASE", Types.NUMERIC),
                        new SqlParameter("P_INGRESOBONIFICACIONDECRETO", Types.NUMERIC),
                        new SqlParameter("P_INGRESOOTROSINGRESOS", Types.NUMERIC),
                        new SqlParameter("P_DESCUENTOIGSS", Types.NUMERIC),
                        new SqlParameter("P_DESCUENTOISR", Types.NUMERIC),
                        new SqlParameter("P_DESCUENTOINASISTENCIAS", Types.NUMERIC),
                        new SqlParameter("P_FECHAMODIFICACION", Types.TIMESTAMP),
                        new SqlParameter("P_USUARIOMODIFICACION", Types.VARCHAR));

        this.callEliminar = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PR_EMPLEADO_ELIMINAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("P_IDEMPLEADO", Types.NUMERIC));

        this.callPersonasCombo = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_PERSONA_COMBO")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter(RETORNO, Types.REF_CURSOR, personaComboRowMapper));
    }

    /**
     * Personas para el combo de la pantalla.
     * PROVISIONAL mientras la pantalla de Personas no exponga su endpoint.
     */
    @SuppressWarnings("unchecked")
    public List<PersonaComboDTO> findPersonasCombo() {
        Map<String, Object> resultado = callPersonasCombo.execute();
        return (List<PersonaComboDTO>) resultado.get(RETORNO);
    }

    @SuppressWarnings("unchecked")
    public List<Empleado> findAll() {
        Map<String, Object> resultado = callListar.execute();
        return (List<Empleado>) resultado.get(RETORNO);
    }

    @SuppressWarnings("unchecked")
    public Optional<Empleado> findById(Integer idEmpleado) {
        Map<String, Object> resultado = callPorId.execute(
                new MapSqlParameterSource().addValue("P_IDEMPLEADO", idEmpleado));
        List<Empleado> filas = (List<Empleado>) resultado.get(RETORNO);
        return filas == null || filas.isEmpty() ? Optional.empty() : Optional.of(filas.get(0));
    }

    /** Status a los que este empleado puede moverse ahora, segun FLUJO_STATUS_EMPLEADO. */
    @SuppressWarnings("unchecked")
    public List<StatusPermitidoDTO> findStatusPermitidos(Integer idEmpleado) {
        Map<String, Object> resultado = callStatusPermitidos.execute(
                new MapSqlParameterSource().addValue("P_IDEMPLEADO", idEmpleado));
        return (List<StatusPermitidoDTO>) resultado.get(RETORNO);
    }

    /** true si el empleado puede pasar a ese status (quedarse igual siempre vale). */
    public boolean esTransicionValida(Integer idEmpleado, Integer idStatusNuevo) {
        Map<String, Object> resultado = callTransicionValida.execute(
                new MapSqlParameterSource()
                        .addValue("P_IDEMPLEADO", idEmpleado)
                        .addValue("P_IDSTATUSNUEVO", idStatusNuevo));

        Object valor = resultado.get(RETORNO);
        return valor instanceof Number numero && numero.intValue() == 1;
    }

    public Integer save(Empleado empleado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_IDPERSONA", empleado.getIdPersona())
                .addValue("P_IDSUCURSAL", empleado.getIdSucursal())
                .addValue("P_FECHACONTRATACION", Date.valueOf(empleado.getFechaContratacion()))
                .addValue("P_IDPUESTO", empleado.getIdPuesto())
                .addValue("P_IDSTATUSEMPLEADO", empleado.getIdStatusEmpleado())
                .addValue("P_INGRESOSUELDOBASE", empleado.getIngresoSueldoBase())
                .addValue("P_INGRESOBONIFICACIONDECRETO", empleado.getIngresoBonificacionDecreto())
                .addValue("P_INGRESOOTROSINGRESOS", empleado.getIngresoOtrosIngresos())
                .addValue("P_DESCUENTOIGSS", empleado.getDescuentoIgss())
                .addValue("P_DESCUENTOISR", empleado.getDescuentoIsr())
                .addValue("P_DESCUENTOINASISTENCIAS", empleado.getDescuentoInasistencias())
                .addValue("P_FECHACREACION", Timestamp.valueOf(empleado.getFechaCreacion()))
                .addValue("P_USUARIOCREACION", empleado.getUsuarioCreacion());

        Map<String, Object> resultado = callInsertar.execute(params);
        Object id = resultado.get("P_IDEMPLEADO");
        return id instanceof Number numero ? numero.intValue() : null;
    }

    /**
     * El procedimiento valida la transicion de status antes de escribir y
     * lanza ORA-20202 si no esta permitida. El Service ya la verifico para
     * dar un mensaje mejor; esta es la red de seguridad.
     */
    public void update(Empleado empleado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_IDEMPLEADO", empleado.getIdEmpleado())
                .addValue("P_IDPERSONA", empleado.getIdPersona())
                .addValue("P_IDSUCURSAL", empleado.getIdSucursal())
                .addValue("P_FECHACONTRATACION", Date.valueOf(empleado.getFechaContratacion()))
                .addValue("P_IDPUESTO", empleado.getIdPuesto())
                .addValue("P_IDSTATUSEMPLEADO", empleado.getIdStatusEmpleado())
                .addValue("P_INGRESOSUELDOBASE", empleado.getIngresoSueldoBase())
                .addValue("P_INGRESOBONIFICACIONDECRETO", empleado.getIngresoBonificacionDecreto())
                .addValue("P_INGRESOOTROSINGRESOS", empleado.getIngresoOtrosIngresos())
                .addValue("P_DESCUENTOIGSS", empleado.getDescuentoIgss())
                .addValue("P_DESCUENTOISR", empleado.getDescuentoIsr())
                .addValue("P_DESCUENTOINASISTENCIAS", empleado.getDescuentoInasistencias())
                .addValue("P_FECHAMODIFICACION", Timestamp.valueOf(empleado.getFechaModificacion()))
                .addValue("P_USUARIOMODIFICACION", empleado.getUsuarioModificacion());

        callActualizar.execute(params);
    }

    /**
     * El procedimiento rechaza con mensaje claro si el empleado tiene
     * liquidacion, inasistencias o cuentas bancarias (ORA-20204/-20205/-20206),
     * y con ORA-20207 si no existe. GlobalExceptionHandler los convierte en 409.
     */
    public void deleteById(Integer idEmpleado) {
        callEliminar.execute(new MapSqlParameterSource().addValue("P_IDEMPLEADO", idEmpleado));
    }

    /** Montos a cero, para no mandar NULL a columnas NOT NULL. */
    public static BigDecimal valorOCero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
