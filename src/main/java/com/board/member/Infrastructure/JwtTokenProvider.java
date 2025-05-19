package com.board.member.Infrastructure;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.board.global.resolver.exception.TokenExpirationException;
import com.board.global.resolver.exception.TokenInvalidException;
import com.board.global.resolver.exception.TokenVerificationException;
import com.board.member.domain.auth.TokenProvider;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final Algorithm algorithm;
    private final long expirationPeriod;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expiration-period}") long expirationPeriod) {
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

    @Override
    public Long extractMemberId(String token) {
        try {
            return extractToken(token);
        } catch (JWTDecodeException e) {
            throw new TokenVerificationException();
        } catch (TokenExpiredException e) {
            throw new TokenExpirationException();
        }
    }

    private Date issuedDate() {
        return new Date(System.currentTimeMillis());
    }

    private Date expiredDate() {
        return new Date(System.currentTimeMillis() + expirationPeriod * 1000L); //2시간
    }

    private Long extractToken(String token) {
        return verifyToken(token).getClaim("memberId")
                .asLong();
    }

    private DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        return Optional.of(verifier.verify(token))
                .orElseThrow(TokenInvalidException::new);
    }
}
