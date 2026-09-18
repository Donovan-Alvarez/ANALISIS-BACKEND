package com.analisis_sistemas.erp.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Punto unico de traduccion de excepciones a respuestas JSON.
 * <p>
 * Objetivo: el cliente (frontend) siempre recibe un mensaje claro y seguro en
 * "message", nunca un stacktrace, nombre de clase interna, sentencia SQL o
 * detalle de Oracle. Toda excepcion no prevista se registra completa en el
 * log del servidor (con logger.error) para que quede disponible para
 * diagnostico, pero al cliente solo llega un mensaje generico.
 * <p>
 * Caso especial: las reglas de negocio definidas en triggers/procedimientos
 * de Oracle usan RAISE_APPLICATION_ERROR con codigos en el rango reservado
 * ORA-20000 a ORA-20999 (ver database/01-schema-completo.sql). Esos mensajes
 * SI estan pensados para el usuario final (p. ej. "No se puede eliminar la
 * empresa: tiene sucursales asociadas."), asi que se exponen tal cual, ya
 * limpios del prefijo "ORA-XXXXX:" y de las lineas ORA-06512 de traza PL/SQL.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String MENSAJE_GENERICO = "Ocurrió un error al procesar tu solicitud. Intenta nuevamente.";
    private static final String MENSAJE_DATOS_RELACIONADOS =
            "No se pudo completar la operación porque el registro tiene datos relacionados.";
    private static final String MENSAJE_SOLICITUD_INVALIDA = "La solicitud enviada no tiene un formato válido.";

    private static final int ORA_APPLICATION_ERROR_MIN = 20000;
    private static final int ORA_APPLICATION_ERROR_MAX = 20999;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex,
                                                                      HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String mensaje = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return construirRespuesta(status, mensaje, request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(MethodArgumentNotValidException ex,
                                                                  HttpServletRequest request) {
        List<Map<String, String>> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapearErrorDeCampo)
                .toList();
        String mensaje = "Revisa los campos marcados: hay " + errores.size() + " error(es) de validación.";
        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje, request, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(HttpMessageNotReadableException ex,
                                                                    HttpServletRequest request) {
        log.warn("Cuerpo de solicitud invalido en {}: {}", request.getRequestURI(), ex.getMessage());
        return construirRespuesta(HttpStatus.BAD_REQUEST, MENSAJE_SOLICITUD_INVALIDA, request, null);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException ex,
                                                                  HttpServletRequest request) {
        SQLException sqlEx = extraerSqlException(ex);

        if (sqlEx != null && esErrorDeNegocioOracle(sqlEx.getErrorCode())) {
            log.warn("Regla de negocio de base de datos rechazó la operación en {}: {}",
                    request.getRequestURI(), sqlEx.getMessage());
            return construirRespuesta(HttpStatus.CONFLICT, limpiarMensajeOracle(sqlEx.getMessage()), request, null);
        }

        log.error("Error de acceso a datos no controlado en {}", request.getRequestURI(), ex);
        return construirRespuesta(HttpStatus.CONFLICT, MENSAJE_DATOS_RELACIONADOS, request, null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleEstadoInvalido(IllegalStateException ex,
                                                                      HttpServletRequest request) {
        log.error("Estado inválido en {}: {}", request.getRequestURI(), ex.getMessage());
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, MENSAJE_GENERICO, request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, MENSAJE_GENERICO, request, null);
    }

    private Map<String, String> mapearErrorDeCampo(FieldError error) {
        Map<String, String> mapa = new LinkedHashMap<>();
        mapa.put("field", error.getField());
        mapa.put("defaultMessage", error.getDefaultMessage());
        return mapa;
    }

    private boolean esErrorDeNegocioOracle(int codigoError) {
        return codigoError >= ORA_APPLICATION_ERROR_MIN && codigoError <= ORA_APPLICATION_ERROR_MAX;
    }

    private SQLException extraerSqlException(Throwable ex) {
        Throwable actual = ex;
        while (actual != null) {
            if (actual instanceof SQLException sqlEx) {
                return sqlEx;
            }
            actual = actual.getCause();
        }
        return null;
    }

    /**
     * Un SQLException de Oracle para RAISE_APPLICATION_ERROR(-20012, 'mensaje')
     * trae un mensaje multilinea como:
     * "ORA-20012: mensaje\nORA-06512: at ..., line N\nORA-06512: at line 1"
     * Aca nos quedamos solo con el mensaje de negocio de la primera linea.
     */
    private String limpiarMensajeOracle(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return MENSAJE_GENERICO;
        }
        String primeraLinea = mensaje.lines().findFirst().orElse(mensaje);
        String limpio = primeraLinea.replaceFirst("^ORA-\\d+:\\s*", "").trim();
        return limpio.isEmpty() ? MENSAJE_GENERICO : limpio;
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje,
                                                                     HttpServletRequest request,
                                                                     List<Map<String, String>> errores) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensaje);
        if (errores != null) {
            body.put("errors", errores);
        }
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
