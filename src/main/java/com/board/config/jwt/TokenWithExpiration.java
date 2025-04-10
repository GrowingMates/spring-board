package com.board.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenWithExpiration {
    private final String token;
    private final long expiration;
}
