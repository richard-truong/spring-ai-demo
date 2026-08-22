package com.eshop.app.adapter.out.security;

import com.eshop.app.infrastructure.security.VerifiedToken;
import com.eshop.core.domain.vo.Role;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET =
        "test-secret-key-0123456789abcdef0123456789abcdef0123456789abcdef";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3600);

    @Test
    void verifyReturnsClaimsForValidToken() {
        String token = provider.issue("user-1", "alice@example.com", Role.CUSTOMER).accessToken();

        VerifiedToken verified = provider.verify(token);

        assertThat(verified.userId()).isEqualTo("user-1");
        assertThat(verified.email()).isEqualTo("alice@example.com");
        assertThat(verified.role()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void verifyRejectsExpiredToken() {
        String token = Jwts.builder()
            .subject("user-1")
            .claim("email", "alice@example.com")
            .claim("role", "CUSTOMER")
            .issuedAt(Date.from(Instant.now().minusSeconds(120)))
            .expiration(Date.from(Instant.now().minusSeconds(60)))
            .signWith(KEY)
            .compact();

        assertThatThrownBy(() -> provider.verify(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void verifyRejectsInvalidSignature() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
            "another-secret-key-0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
        String token = signedBy(otherKey).compact();

        assertThatThrownBy(() -> provider.verify(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void verifyRejectsMissingSubject() {
        String token = signedBy(KEY)
            .claim("email", "alice@example.com")
            .claim("role", "CUSTOMER")
            .compact();

        assertThatThrownBy(() -> provider.verify(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void verifyRejectsMissingEmail() {
        String token = signedBy(KEY)
            .subject("user-1")
            .claim("role", "CUSTOMER")
            .compact();

        assertThatThrownBy(() -> provider.verify(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void verifyRejectsMissingRole() {
        String token = signedBy(KEY)
            .subject("user-1")
            .claim("email", "alice@example.com")
            .compact();

        assertThatThrownBy(() -> provider.verify(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void verifyRejectsUnknownRole() {
        String token = signedBy(KEY)
            .subject("user-1")
            .claim("email", "alice@example.com")
            .claim("role", "ADMIN")
            .compact();

        assertThatThrownBy(() -> provider.verify(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void verifyRejectsMalformedToken() {
        assertThatThrownBy(() -> provider.verify("not-a-jwt"))
            .isInstanceOf(JwtException.class);
    }

    @Test
    void constructorRejectsMissingSecret() {
        assertThatThrownBy(() -> new JwtTokenProvider("   ", 3600))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void constructorRejectsShortSecret() {
        assertThatThrownBy(() -> new JwtTokenProvider("too-short", 3600))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void constructorRejectsNonPositiveExpiration() {
        assertThatThrownBy(() -> new JwtTokenProvider(SECRET, 0))
            .isInstanceOf(IllegalStateException.class);
    }

    private JwtBuilder signedBy(SecretKey key) {
        return Jwts.builder()
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(60)))
            .signWith(key);
    }

}
