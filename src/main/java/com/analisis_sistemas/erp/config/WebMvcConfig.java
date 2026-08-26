package com.analisis_sistemas.erp.config;

import com.analisis_sistemas.erp.security.PermisoAccionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra PermisoAccionInterceptor solo sobre /api/**. Fuera de ese prefijo
 * (/auth/login, /auth/logout, /auth/recuperar-password/**, /error) el
 * interceptor ni se evalua: la exclusion es estructural, no una lista manual
 * que alguien pueda olvidar actualizar. Ver PLAN_AUTORIZACION_RBAC_2026-08-25.md.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final PermisoAccionInterceptor permisoAccionInterceptor;

    public WebMvcConfig(PermisoAccionInterceptor permisoAccionInterceptor) {
        this.permisoAccionInterceptor = permisoAccionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permisoAccionInterceptor)
                .addPathPatterns("/api/**");
    }
}
