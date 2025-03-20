package com.board.member.domain.auth;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface TokenProvider {

    String create(Long memberId);
    DecodedJWT verifyToken(String token);
}
