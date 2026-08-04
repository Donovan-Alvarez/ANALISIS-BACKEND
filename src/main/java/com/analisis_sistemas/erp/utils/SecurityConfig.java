package com.analisis_sistemas.erp.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // deshabilitado: usaremos JWT, no cookies/sesiones
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // TEMPORAL: todo abierto mientras probamos
                );
        return http.build();
    }
}
