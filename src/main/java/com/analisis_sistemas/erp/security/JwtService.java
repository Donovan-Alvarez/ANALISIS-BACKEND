package com.analisis_sistemas.erp.security;

import com.analisis_sistemas.erp.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String idUsuario, Integer idRole, String nombre, String nombreRole, String idSesion) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(idUsuario)
                .claim("idRole", idRole)
                .claim("nombre", nombre)
                .claim("nombreRole", nombreRole)
                .claim("idSesion", idSesion)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getIdUsuarioFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Integer getIdRoleFromToken(String token) {
        return parseClaims(token).get("idRole", Integer.class);
    }

    public String getNombreRoleFromToken(String token) {
        return parseClaims(token).get("nombreRole", String.class);
    }

    public String getIdSesionFromToken(String token) {
        return parseClaims(token).get("idSesion", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
