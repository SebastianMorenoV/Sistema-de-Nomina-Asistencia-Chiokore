package com.chiokore.asistencianomina.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final RsaKeyProvider rsaKeyProvider;
    private final long accessExpiration;
    private final String issuer;

    public JwtService(RsaKeyProvider rsaKeyProvider,
                      @Value("${jwt.access-expiration:86400}") long accessExpiration,
                      @Value("${jwt.issuer:chiokore-nomina-idp}") String issuer) {
        this.rsaKeyProvider = rsaKeyProvider;
        this.accessExpiration = accessExpiration;
        this.issuer = issuer;
    }

    public String generateAccessToken(String username, Long empleadoId) {
        return generateAccessToken(username, empleadoId, null, false);
    }

    public String generateAccessToken(String username, Long empleadoId, String rol, boolean kiosk) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("empleadoId", empleadoId)
                .claim("rol", rol)
                .claim("kiosk", kiosk)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpiration * 1000))
                .header().keyId(rsaKeyProvider.getKeyId()).and()
                .signWith(rsaKeyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(rsaKeyProvider.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractEmpleadoId(String token) {
        return extractAllClaims(token).get("empleadoId", Long.class);
    }

    public String extractRol(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    public boolean isKioskToken(String token) {
        Boolean kiosk = extractAllClaims(token).get("kiosk", Boolean.class);
        return Boolean.TRUE.equals(kiosk);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getPublicKeyPem() {
        return rsaKeyProvider.getPublicKeyPem();
    }

    public String getKeyId() {
        return rsaKeyProvider.getKeyId();
    }

    public String getIssuer() {
        return issuer;
    }

    public boolean isUsingEphemeralKeyPair() {
        return rsaKeyProvider.isEphemeral();
    }
}
