package com.analisis_sistemas.erp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Map;

/**
 * Acceso a la tabla SESION a traves de sus procedimientos almacenados.
 *
 * NOTA: este archivo se reconstruyo a partir de como lo usan AuthService y
 * JwtAuthenticationFilter, porque no venia en el commit que subio esa
 * funcionalidad y el proyecto no compilaba sin el. Las firmas salen del DDL
 * en database/01-schema-completo.sql (seccion 6 y 7). Si quien escribio la
 * version original la tiene, conviene comparar antes de dar esto por bueno.
 */
@Repository
public class SesionRepository {

    private final SimpleJdbcCall callIniciarSesion;
    private final SimpleJdbcCall callCerrarSesion;
    private final SimpleJdbcCall callValidarSesion;

    public SesionRepository(JdbcTemplate jdbcTemplate) {
        // Mismo criterio que TokenRecuperacionRepository: los parametros se
        // declaran explicitos (en MAYUSCULAS, como los guarda Oracle) en vez de
        // dejar que SimpleJdbcCall los resuelva por metadata.
        this.callIniciarSesion = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PR_SESION_INICIAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_IDUSUARIO", Types.VARCHAR),
                        new SqlParameter("P_IDSESION", Types.VARCHAR),
                        new SqlParameter("P_DIRECCIONIP", Types.VARCHAR),
                        new SqlParameter("P_HTTPUSERAGENT", Types.VARCHAR)
                );

        this.callCerrarSesion = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PR_SESION_CERRAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_IDSESION", Types.VARCHAR)
                );

        this.callValidarSesion = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_SESION_VALIDAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("RETURN_VALUE", Types.NUMERIC),
                        new SqlParameter("P_IDSESION", Types.VARCHAR)
                );
    }

    /**
     * Abre una sesion nueva y cierra cualquier otra que el usuario tuviera
     * activa (una sesion por usuario). PR_SESION_INICIAR delega en
     * TRG_SESION_INICIO el calculo de FechaExpiracion/MinutosVigencia y en
     * TRG_SESION_SYNC_USUARIO la actualizacion de USUARIO.SesionActual.
     */
    public void iniciarSesion(String idUsuario, String idSesion, String direccionIp, String httpUserAgent) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_IDUSUARIO", idUsuario)
                .addValue("P_IDSESION", idSesion)
                .addValue("P_DIRECCIONIP", direccionIp)
                .addValue("P_HTTPUSERAGENT", httpUserAgent);
        callIniciarSesion.execute(params);
    }

    public void cerrarSesion(String idSesion) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_IDSESION", idSesion);
        callCerrarSesion.execute(params);
    }

    /**
     * Devuelve true si la sesion sigue viva. Ademas desliza su vencimiento
     * (sliding expiration), por eso se llama en cada request autenticado.
     */
    public boolean validarSesion(String idSesion) {
        if (idSesion == null || idSesion.isBlank()) {
            return false;
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_IDSESION", idSesion);
        Map<String, Object> result = callValidarSesion.execute(params);

        Object valor = result.get("RETURN_VALUE");
        if (valor instanceof BigDecimal decimal) {
            return decimal.intValue() == 1;
        }
        return valor instanceof Number numero && numero.intValue() == 1;
    }
}
