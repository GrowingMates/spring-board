package com.board.config.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUtil {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String COOKIE_NAME = "token";
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1시간

    private final JwtProperties jwtProperties;

    public String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseGet(() -> extractFromHeader(request)); // 없으면 헤더에서 추출
        }
        return extractFromHeader(request); // 쿠키 자체가 없으면 바로 헤더에서 추출
    }

    private String extractFromHeader(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public TokenWithExpiration generateTokenWithExpiration(String subject) {
        return new TokenWithExpiration(generateToken(subject), ACCESS_TOKEN_EXPIRATION);
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // sub : 이메일(jwt 주인)
                .setIssuer(jwtProperties.getIssuer())  // Issuer 설정 (필수아님)
                .setIssuedAt(new Date()) // 발급시간 (필수아님)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION)) // 유효시간 필수!
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

    private byte[] getSigningKey() {
        return jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
    }
}
