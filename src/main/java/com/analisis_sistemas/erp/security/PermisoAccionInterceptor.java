package com.analisis_sistemas.erp.security;

import com.analisis_sistemas.erp.repository.OpcionRepository;
import com.analisis_sistemas.erp.repository.RoleOpcionRepository;
import com.analisis_sistemas.erp.repository.RoleOpcionRepository.PermisoFlags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * Autorizacion por accion (RBAC) contra ROLE_OPCION, centralizada para los
 * ~12 controllers de /api/**. Cubre el vacio detectado el 2026-08-25 (ver
 * REPORTE_AUTORIZACION_ACCION_2026-08-25.md): SecurityConfig solo exige un
 * JWT valido (autenticacion), pero ningun controller verificaba si el rol
 * del usuario tenia permiso para la accion puntual (Alta/Baja/Cambio).
 * <p>
 * Se registra sobre /api/** en WebMvcConfig, no en cada controller. GET no
 * se bloquea aqui: "Consultar" ya se resuelve por existencia de fila y se
 * filtra en el menu (MenuUsuarioController).
 * <p>
 * Diseno completo en PLAN_AUTORIZACION_RBAC_2026-08-25.md.
 */
@Component
public class PermisoAccionInterceptor implements HandlerInterceptor {

    private static final String MENSAJE_SIN_PERMISO = "No tiene permiso para realizar esta acción.";

    // Excepciones al patron general "primer segmento de /api/** = slug de Pagina".
    // Hoy solo existe una: RoleOpcionController vive bajo /api/roles/... pero
    // administra la opcion "asignacion-permisos", no "roles".
    private static final Map<String, String> EXCEPCIONES_PATRON = Map.of(
            "/api/roles/{idRole}/opciones/por-modulo/{idModulo}", "asignacion-permisos"
    );

    private final OpcionRepository opcionRepository;
    private final RoleOpcionRepository roleOpcionRepository;

    public PermisoAccionInterceptor(OpcionRepository opcionRepository, RoleOpcionRepository roleOpcionRepository) {
        this.opcionRepository = opcionRepository;
        this.roleOpcionRepository = roleOpcionRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true; // recursos estaticos, etc.: no hay accion de negocio que autorizar
        }

        String metodo = request.getMethod();
        if (!"POST".equals(metodo) && !"PUT".equals(metodo) && !"DELETE".equals(metodo)) {
            return true; // GET/HEAD/OPTIONS
        }

        String patron = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String slug = resolverSlug(patron);

        // Fail-closed deliberado (plan, punto 5.a): si la URL no resuelve a una
        // OPCION conocida, se corta con 500 en vez de dejar pasar la peticion.
        // Es el mismo tipo de vacio que origino este cambio (un endpoint mutante
        // sin nada que lo controle), asi que mejor romper ruidoso en desarrollo.
        Integer idOpcion = opcionRepository.findIdOpcionPorPagina(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "No existe una OPCION configurada para la pagina '" + slug + "' (patron " + patron + ")."));

        Integer idRole = idRoleAutenticado();

        // Ausencia de fila en ROLE_OPCION = todo en false, nunca "permitido por
        // defecto". Es exactamente el caso real detectado: un rol con solo Alta
        // asignado en Sucursales podia igual editar/eliminar porque nada
        // consultaba ROLE_OPCION antes de ejecutar la accion.
        PermisoFlags flags = roleOpcionRepository.findFlags(idRole, idOpcion)
                .orElse(new PermisoFlags(false, false, false));

        boolean permitido = switch (metodo) {
            case "POST" -> flags.alta();
            case "PUT" -> flags.cambio();
            case "DELETE" -> flags.baja();
            default -> false; // inalcanzable, ya se filtro arriba
        };

        if (!permitido) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MENSAJE_SIN_PERMISO);
        }

        return true;
    }

    private String resolverSlug(String patron) {
        if (patron == null) {
            return null;
        }

        String excepcion = EXCEPCIONES_PATRON.get(patron);
        if (excepcion != null) {
            return excepcion;
        }

        String sinPrefijo = patron.startsWith("/api/") ? patron.substring("/api/".length()) : patron;
        int barra = sinPrefijo.indexOf('/');
        return barra >= 0 ? sinPrefijo.substring(0, barra) : sinPrefijo;
    }

    private Integer idRoleAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof Integer idRole)) {
            // No deberia pasar: SecurityConfig ya exige authenticated() antes de
            // llegar aca, y JwtAuthenticationFilter siempre setea idRole en los
            // details al autenticar. Si ocurre, es una inconsistencia de config.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MENSAJE_SIN_PERMISO);
        }
        return idRole;
    }
}
