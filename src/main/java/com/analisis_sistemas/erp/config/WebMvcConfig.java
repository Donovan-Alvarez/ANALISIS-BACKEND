package com.analisis_sistemas.erp.config;

import com.analisis_sistemas.erp.security.PermisoAccionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
