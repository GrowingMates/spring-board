package com.board.member.Infrastructure.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.board.member.domain.auth.TokenProvider;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final Algorithm algorithm;
    private final long expirationPeriod;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration-period}") long expirationPeriod) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.expirationPeriod = expirationPeriod;
    }

    @Override
    public String create(Long memberId) {
        return JWT.create()
                .withClaim("memberId", memberId)
                .withIssuedAt(issuedDate())
                .withExpiresAt(expiredDate())
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    private Date issuedDate() {
        return new Date(System.currentTimeMillis());
    }

    private Date expiredDate() {
        return new Date(System.currentTimeMillis() + expirationPeriod * 1000L); //2시간
    }

    @Override
    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        return verifier.verify(token);
    }
}
