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

@Repository
public class SesionRepository {

    private final SimpleJdbcCall callIniciarSesion;
    private final SimpleJdbcCall callCerrarSesion;
    private final SimpleJdbcCall callValidarSesion;

    public SesionRepository(JdbcTemplate jdbcTemplate) {
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
