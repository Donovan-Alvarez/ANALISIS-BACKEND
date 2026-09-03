package com.analisis_sistemas.erp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Map;

@Repository
public class TokenRecuperacionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcCall callGenerarToken;
    private final SimpleJdbcCall callValidarToken;

    public TokenRecuperacionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.callGenerarToken = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PR_TOKEN_RECUPERACION_GENERAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_IDUSUARIO", Types.VARCHAR),
                        new SqlParameter("P_IDTOKEN", Types.VARCHAR),
                        new SqlParameter("P_DIRECCIONIP", Types.VARCHAR)
                );

        this.callValidarToken = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName("FN_TOKEN_RECUPERACION_VALIDAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("RETURN_VALUE", Types.VARCHAR),
                        new SqlParameter("P_IDTOKEN", Types.VARCHAR)
                );
    }

    public void generarToken(String idUsuario, String idToken, String direccionIp) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_IDUSUARIO", idUsuario)
                .addValue("P_IDTOKEN", idToken)
                .addValue("P_DIRECCIONIP", direccionIp);
        callGenerarToken.execute(params);
    }

    public String validarToken(String idToken) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_IDTOKEN", idToken);
        Map<String, Object> result = callValidarToken.execute(params);
        return (String) result.get("RETURN_VALUE");
    }

    public void marcarTokenUtilizado(String idToken) {
        String sql = "UPDATE TOKEN_RECUPERACION_PASSWORD SET Utilizado = 1 WHERE IdToken = ?";
        jdbcTemplate.update(sql, idToken);
    }
}
