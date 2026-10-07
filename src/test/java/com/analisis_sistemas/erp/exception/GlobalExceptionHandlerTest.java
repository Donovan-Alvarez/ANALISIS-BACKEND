package com.analisis_sistemas.erp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba unitaria del mapeo de errores Oracle -> HTTP, sin base de datos:
 * se simula el SQLException que lanzaria el driver (mismo errorCode y texto
 * "ORA-xxxxx: ...") envuelto en la DataAccessException que arma Spring JDBC.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/bancos");

    private static SQLException oracle(int codigo, String mensaje) {
        return new SQLIntegrityConstraintViolationException(mensaje, "23000", codigo);
    }

    private ResponseEntity<Map<String, Object>> manejar(SQLException sqlEx) {
        return handler.handleDataAccess(new DataIntegrityViolationException("envoltura de Spring", sqlEx), request);
    }

    @Test
    void ora00001_uniqueViolado_devuelve409ConMensajeFijo() {
        ResponseEntity<Map<String, Object>> r = handler.handleDataAccess(
                new DuplicateKeyException("envoltura de Spring",
                        oracle(1, "ORA-00001: unique constraint (SEGURIDAD_USER.UQ_BANCO_NOMBRE) violated")),
                request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(r.getBody()).containsEntry("message", "Ya existe un registro con los mismos datos.");
        assertThat(r.getBody().get("message").toString()).doesNotContain("UQ_BANCO_NOMBRE");
    }

    @Test
    void ora02291_padreNoExiste_devuelve400() {
        ResponseEntity<Map<String, Object>> r = manejar(
                oracle(2291, "ORA-02291: integrity constraint (SEGURIDAD_USER.FK_PUESTO_DEPARTAMENTO) violated - parent key not found"));

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody()).containsEntry("message", "El registro relacionado seleccionado no existe.");
    }

    @Test
    void ora12899_valorDemasiadoLargo_devuelve400() {
        ResponseEntity<Map<String, Object>> r = manejar(
                oracle(12899, "ORA-12899: value too large for column \"SEGURIDAD_USER\".\"BANCO\".\"NOMBRE\" (actual: 80, maximum: 50)"));

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody()).containsEntry("message", "Un valor excede la longitud permitida.");
    }

    @Test
    void ora01400_nullEnObligatorio_devuelve400() {
        ResponseEntity<Map<String, Object>> r = manejar(
                oracle(1400, "ORA-01400: cannot insert NULL into (\"SEGURIDAD_USER\".\"BANCO\".\"NOMBRE\")"));

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody()).containsEntry("message", "Falta un dato obligatorio.");
    }

    @Test
    void ora20xxx_reglaDeNegocio_sigueIgual409ConMensajeLimpio() {
        ResponseEntity<Map<String, Object>> r = manejar(new SQLException(
                "ORA-20107: No se puede eliminar el banco: tiene 100 cuenta(s) bancaria(s) de empleados asociada(s).\n"
                        + "ORA-06512: at \"SEGURIDAD_USER.TRG_BANCO_BAJA_VALIDA\", line 6\n"
                        + "ORA-04088: error during execution of trigger 'SEGURIDAD_USER.TRG_BANCO_BAJA_VALIDA'",
                "72000", 20107));

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(r.getBody()).containsEntry("message",
                "No se puede eliminar el banco: tiene 100 cuenta(s) bancaria(s) de empleados asociada(s).");
    }

    @Test
    void otroErrorOracle_sigueIgual409Generico() {
        ResponseEntity<Map<String, Object>> r = manejar(
                oracle(2292, "ORA-02292: integrity constraint (SEGURIDAD_USER.FK_X) violated - child record found"));

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(r.getBody()).containsEntry("message",
                "No se pudo completar la operación porque el registro tiene datos relacionados.");
    }

    @Test
    void respuestaConservaFormato() {
        ResponseEntity<Map<String, Object>> r = manejar(oracle(1400, "ORA-01400: cannot insert NULL"));

        assertThat(r.getBody()).containsKeys("timestamp", "status", "error", "message", "path");
        assertThat(r.getBody()).containsEntry("status", 400).containsEntry("path", "/api/bancos");
    }
}
