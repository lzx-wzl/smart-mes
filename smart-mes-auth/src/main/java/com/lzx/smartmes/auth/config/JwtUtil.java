package com.lzx.smartmes.auth.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final byte[] key;

    private final long expiration;

    public JwtUtil(
            @Value("${smart-mes.jwt.secret:smart-mes-development-secret-must-be-at-least-32-bytes}")
            String secret,

            @Value("${smart-mes.jwt.expiration-ms:7200000}")
            long expiration
    ) {
        this.key = secret.getBytes(StandardCharsets.UTF_8);
        this.expiration = expiration;
    }

    public String create(String username) {

        Date now = new Date();

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(
                        new Date(
                                now.getTime() + expiration
                        )
                )
                .signWith(
                        Keys.hmacShaKeyFor(key)
                )
                .compact();
    }

    public String username(String token) {

        return Jwts.parser()
                .verifyWith(
                        Keys.hmacShaKeyFor(key)
                )
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}