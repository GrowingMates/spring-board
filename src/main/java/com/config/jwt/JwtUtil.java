package com.config.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtUtil {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7일

    private final JwtProperties jwtProperties;

    public String extractFromHeader(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public TokenWithExpiration generateAccessToken(Long memberId) {
        String token = generateToken(memberId, ACCESS_TOKEN_EXPIRATION);
        return new TokenWithExpiration(token, ACCESS_TOKEN_EXPIRATION);
    }

    public TokenWithExpiration generateRefreshToken(Long memberId) {
        String token = generateToken(memberId, REFRESH_TOKEN_EXPIRATION);
        return new TokenWithExpiration(token, REFRESH_TOKEN_EXPIRATION);
    }

    public String generateToken(Long memberId, long expirationTime) {
        return Jwts.builder()
                .setSubject(String.valueOf(memberId)) // sub : 이메일(jwt 주인)
                .setIssuer(jwtProperties.getIssuer())  // Issuer 설정 (필수아님)
                .setIssuedAt(new Date()) // 발급시간 (필수아님)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 유효시간 필수!
                .signWith(Keys.hmacShaKeyFor(getSigningKey()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractMemberIdFromToken(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(subject);
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

    private byte[] getSigningKey() {
        return jwtProperties.getSecretKey()
                .getBytes(StandardCharsets.UTF_8);
    }
}
