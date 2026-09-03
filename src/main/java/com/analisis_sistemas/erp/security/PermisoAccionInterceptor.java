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

@Component
public class PermisoAccionInterceptor implements HandlerInterceptor {

    private static final String MENSAJE_SIN_PERMISO = "No tiene permiso para realizar esta acción.";

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
            return true;
        }

        String metodo = request.getMethod();
        if (!"POST".equals(metodo) && !"PUT".equals(metodo) && !"DELETE".equals(metodo)) {
            return true;
        }

        String patron = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String slug = resolverSlug(patron);

        Integer idOpcion = opcionRepository.findIdOpcionPorPagina(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "No existe una OPCION configurada para la pagina '" + slug + "' (patron " + patron + ")."));

        Integer idRole = idRoleAutenticado();

        PermisoFlags flags = roleOpcionRepository.findFlags(idRole, idOpcion)
                .orElse(new PermisoFlags(false, false, false));

        boolean permitido = switch (metodo) {
            case "POST" -> flags.alta();
            case "PUT" -> flags.cambio();
            case "DELETE" -> flags.baja();
            default -> false;
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MENSAJE_SIN_PERMISO);
        }
        return idRole;
    }
}
