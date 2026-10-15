package com.analisis_sistemas.erp.repository;

import com.analisis_sistemas.erp.dto.ResultadoCalculoPlanillaDTO;
import com.analisis_sistemas.erp.entity.PeriodoPlanilla;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CalculoPlanillaRepository {

    private static final String SELECT_PERIODOS = """
            SELECT pp.Anio, pp.Mes, pp.FechaInicio, pp.FechaFin,
                   c.TotalIngresos, c.TotalDescuentos, c.SalarioNeto, c.FechaHoraProcesada,
                   c.UsuarioCreacion AS UsuarioProceso,
                   (SELECT COUNT(*) FROM PLANILLA_DETALLE d
                     WHERE d.Anio = pp.Anio AND d.Mes = pp.Mes) AS EmpleadosCalculados
            FROM PERIODO_PLANILLA pp
            LEFT JOIN PLANILLA_CABECERA c ON c.Anio = pp.Anio AND c.Mes = pp.Mes
            """;

    private final JdbcTemplate jdbcTemplate;
    private final PeriodoPlanillaRowMapper periodoPlanillaRowMapper;
    private final SimpleJdbcCall callCalcularPlanilla;

    public CalculoPlanillaRepository(JdbcTemplate jdbcTemplate, PeriodoPlanillaRowMapper periodoPlanillaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.periodoPlanillaRowMapper = periodoPlanillaRowMapper;

        this.callCalcularPlanilla = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("PR_CALCULAR_PLANILLA")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_ANIO", Types.NUMERIC),
                        new SqlParameter("P_MES", Types.NUMERIC),
                        new SqlParameter("P_USUARIO", Types.VARCHAR),
                        new SqlParameter("P_FECHAPROCESO", Types.TIMESTAMP),
                        new SqlOutParameter("P_EMPLEADOSPROCESADOS", Types.NUMERIC),
                        new SqlOutParameter("P_INASISTENCIASPROCESADAS", Types.NUMERIC),
                        new SqlOutParameter("P_TOTALINGRESOS", Types.NUMERIC),
                        new SqlOutParameter("P_TOTALDESCUENTOS", Types.NUMERIC),
                        new SqlOutParameter("P_SALARIONETO", Types.NUMERIC)
                );
    }

    public List<PeriodoPlanilla> findPeriodos() {
        String sql = SELECT_PERIODOS + "ORDER BY pp.Anio, pp.Mes";
        return jdbcTemplate.query(sql, periodoPlanillaRowMapper);
    }

    public Optional<PeriodoPlanilla> findPeriodo(Integer anio, Integer mes) {
        String sql = SELECT_PERIODOS + "WHERE pp.Anio = ? AND pp.Mes = ?";
        return jdbcTemplate.query(sql, periodoPlanillaRowMapper, anio, mes).stream().findFirst();
    }

    
    public ResultadoCalculoPlanillaDTO calcular(Integer anio, Integer mes, String usuario, LocalDateTime fechaProceso) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ANIO", anio)
                .addValue("P_MES", mes)
                .addValue("P_USUARIO", usuario)
                .addValue("P_FECHAPROCESO", Timestamp.valueOf(fechaProceso));

        Map<String, Object> salida = callCalcularPlanilla.execute(params);

        ResultadoCalculoPlanillaDTO resultado = new ResultadoCalculoPlanillaDTO();
        resultado.setAnio(anio);
        resultado.setMes(mes);
        resultado.setEmpleadosProcesados(aBigDecimal(salida.get("P_EMPLEADOSPROCESADOS")).intValue());
        resultado.setInasistenciasProcesadas(aBigDecimal(salida.get("P_INASISTENCIASPROCESADAS")).intValue());
        resultado.setTotalIngresos(aBigDecimal(salida.get("P_TOTALINGRESOS")));
        resultado.setTotalDescuentos(aBigDecimal(salida.get("P_TOTALDESCUENTOS")));
        resultado.setSalarioNeto(aBigDecimal(salida.get("P_SALARIONETO")));
        resultado.setFechaHoraProcesada(fechaProceso);
        resultado.setUsuario(usuario);
        return resultado;
    }

    private BigDecimal aBigDecimal(Object valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        if (valor instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return new BigDecimal(valor.toString());
    }
}