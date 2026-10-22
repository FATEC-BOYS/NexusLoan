package com.nexusloan.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import com.nexusloan.config.JwtProperties;
import com.nexusloan.entity.User;
import com.nexusloan.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    static final String CLAIM_TENANT = "tenant";
    static final String CLAIM_ROLE = "role";
    static final String CLAIM_EMAIL = "email";

    /** HS256 exige chave de no mínimo 256 bits. */
    private static final int MIN_SECRET_BYTES = 32;

    private final JwtProperties properties;
    private final SecretKey key;
    private final JwtParser parser;

    public JwtService(JwtProperties properties) {
        byte[] secret = properties.secret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "nexusloan.security.jwt.secret deve ter pelo menos " + MIN_SECRET_BYTES + " bytes");
        }
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(secret);
        this.parser = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(properties.issuer())
                .build();
    }

    public String generate(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.issuer())
                .subject(user.getId().toString())
                .claim(CLAIM_TENANT, user.getTenantId().toString())
                .claim(CLAIM_ROLE, user.getRole().name())
                .claim(CLAIM_EMAIL, user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.expiration())))
                .signWith(key)
                .compact();
    }

    public long expirationSeconds() {
        return properties.expiration().toSeconds();
    }

    /**
     * Valida assinatura, emissor e expiração, e devolve o usuário autenticado.
     *
     * @throws JwtException se o token for inválido
     */
    public AuthenticatedUser parse(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();
        try {
            return new AuthenticatedUser(
                    UUID.fromString(claims.getSubject()),
                    UUID.fromString(claims.get(CLAIM_TENANT, String.class)),
                    claims.get(CLAIM_EMAIL, String.class),
                    Role.valueOf(claims.get(CLAIM_ROLE, String.class)));
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new MalformedClaimsException("Claims obrigatórias ausentes ou inválidas", ex);
        }
    }

    static class MalformedClaimsException extends JwtException {
        MalformedClaimsException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
