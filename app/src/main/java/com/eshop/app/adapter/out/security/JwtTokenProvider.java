package com.eshop.app.adapter.out.security;

import com.eshop.app.infrastructure.security.TokenVerifier;
import com.eshop.app.infrastructure.security.VerifiedToken;
import com.eshop.core.application.dto.TokenResult;
import com.eshop.core.application.port.out.TokenProviderPort;
import com.eshop.core.domain.vo.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProviderPort, TokenVerifier {

    private static final String TOKEN_TYPE = "Bearer";

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration}") long expirationSeconds) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("app.jwt.secret must be provided");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes for HS256");
        }
        if (expirationSeconds <= 0) {
            throw new IllegalStateException("app.jwt.expiration must be positive");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public TokenResult issue(String userId, String email, Role role) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirationSeconds);
        String token = Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .claim("role", role.name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(key)
            .compact();
        return new TokenResult(token, TOKEN_TYPE, expirationSeconds);
    }

    @Override
    public VerifiedToken verify(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            String subject = claims.getSubject();
            String email = claims.get("email", String.class);
            String roleValue = claims.get("role", String.class);
            if (subject == null || subject.isBlank()
                || email == null || email.isBlank()
                || roleValue == null || roleValue.isBlank()) {
                throw new JwtException("invalid token claims");
            }
            Role role;
            try {
                role = Role.valueOf(roleValue);
            } catch (IllegalArgumentException e) {
                throw new JwtException("unsupported role", e);
            }
            return new VerifiedToken(subject, email, role);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("invalid token", e);
        }
    }

}
