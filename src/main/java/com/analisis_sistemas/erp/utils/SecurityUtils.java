package com.analisis_sistemas.erp.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private static final String USUARIO_FALLBACK = "system";
    private static final String PRINCIPAL_ANONIMO = "anonymousUser";

    private SecurityUtils() {
    }

    public static String getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || PRINCIPAL_ANONIMO.equals(auth.getName())) {
            return USUARIO_FALLBACK;
        }
        return auth.getName();
    }
}
