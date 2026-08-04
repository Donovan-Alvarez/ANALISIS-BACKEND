package com.analisis_sistemas.erp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String secretKey;
    private Long expirationMs;
}
