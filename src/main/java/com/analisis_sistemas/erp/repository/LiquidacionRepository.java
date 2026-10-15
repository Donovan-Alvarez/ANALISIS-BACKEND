package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.dto.EmpleadoLiquidableDTO;
import com.analisis_sistemas.erp.entity.Liquidacion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Acceso a LIQUIDACION solo por procedimientos almacenados.
 * <p>
 * No hay update ni delete a proposito: una liquidacion emitida es historial
 * permanente del empleado y no se edita (criterio de aceptacion de la
 * pantalla). El calculo completo ocurre dentro de PR_LIQUIDACION_CALCULAR.
 */
@Repository
public class LiquidacionRepository {

    private static final String RETORNO = "RETURN_VALUE";

    private final SimpleJdbcCall callListar;
    private final SimpleJdbcCall callPorId;
    private final SimpleJdbcCall callLiquidables;
    private final SimpleJdbcCall callCalcular;

    public LiquidacionRepository(JdbcTemplate jdbcTemplate,
                                  LiquidacionRowMapper liquidacionRowMapper,
                                  EmpleadoLiquidableRowMapper empleadoLiquidableRowMapper) {

        this.callListar = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_LIQUIDACION_LISTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter(RETORNO, Types.REF_CURSOR, liquidacionRowMapper));

        this.callPorId = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_LIQUIDACION_POR_ID")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter(RETORNO, Types.REF_CURSOR, liquidacionRowMapper),
                        new SqlParameter("P_IDLIQUIDACION", Types.NUMERIC));

        this.callLiquidables = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_EMPLEADO_LIQUIDABLES")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter(RETORNO, Types.REF_CURSOR, empleadoLiquidableRowMapper));

        this.callCalcular = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PR_LIQUIDACION_CALCULAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_IDEMPLEADO", Types.NUMERIC),
                        new SqlParameter("P_FECHAEGRESO", Types.DATE),
                        new SqlParameter("P_FECHALIQUIDACION", Types.DATE),
                        new SqlParameter("P_MOTIVOEGRESO", Types.VARCHAR),
                        new SqlParameter("P_FECHACREACION", Types.DATE),
                        new SqlParameter("P_USUARIOCREACION", Types.VARCHAR),
                        new SqlOutParameter("P_IDLIQUIDACION", Types.NUMERIC));
    }

    @SuppressWarnings("unchecked")
    public List<Liquidacion> findAll() {
        Map<String, Object> resultado = callListar.execute();
        return (List<Liquidacion>) resultado.get(RETORNO);
    }

    @SuppressWarnings("unchecked")
    public Optional<Liquidacion> findById(Integer idLiquidacion) {
        Map<String, Object> resultado = callPorId.execute(
                new MapSqlParameterSource().addValue("P_IDLIQUIDACION", idLiquidacion));
        List<Liquidacion> filas = (List<Liquidacion>) resultado.get(RETORNO);
        return filas == null || filas.isEmpty() ? Optional.empty() : Optional.of(filas.get(0));
    }

    /** Empleados sin liquidacion: los unicos que el combo debe ofrecer. */
    @SuppressWarnings("unchecked")
    public List<EmpleadoLiquidableDTO> findEmpleadosLiquidables() {
        Map<String, Object> resultado = callLiquidables.execute();
        return (List<EmpleadoLiquidableDTO>) resultado.get(RETORNO);
    }

    /**
     * Lee los montos del empleado, calcula los cuatro totales e inserta, todo
     * dentro del procedimiento y en una sola transaccion. Rechaza con
     * ORA-20210 a -20213 si el empleado no existe, ya esta liquidado o las
     * fechas son incoherentes; GlobalExceptionHandler los traduce a 409.
     *
     * @return el id de la liquidacion creada
     */
    public Integer calcular(Integer idEmpleado, LocalDate fechaEgreso, LocalDate fechaLiquidacion,
                             String motivoEgreso, LocalDate fechaCreacion, String usuarioCreacion) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_IDEMPLEADO", idEmpleado)
                .addValue("P_FECHAEGRESO", Date.valueOf(fechaEgreso))
                .addValue("P_FECHALIQUIDACION", Date.valueOf(fechaLiquidacion))
                .addValue("P_MOTIVOEGRESO", motivoEgreso)
                .addValue("P_FECHACREACION", Date.valueOf(fechaCreacion))
                .addValue("P_USUARIOCREACION", usuarioCreacion);

        Map<String, Object> resultado = callCalcular.execute(params);
        Object id = resultado.get("P_IDLIQUIDACION");
        return id instanceof Number numero ? numero.intValue() : null;
    }
}
