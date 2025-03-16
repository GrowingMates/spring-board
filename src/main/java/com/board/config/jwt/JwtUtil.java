package com.board.config.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private byte[] getSigningKey() {
        return jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(String email, long expirationTime) {
        return Jwts.builder()
                .setSubject(email) // sub : 이메일(jwt 주인)
                .setIssuer(jwtProperties.getIssuer())  // Issuer 설정 (필수아님)
                .setIssuedAt(new Date()) // 발급시간 (필수아님)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 유효시간 필수!
                .signWith(Keys.hmacShaKeyFor(getSigningKey()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
