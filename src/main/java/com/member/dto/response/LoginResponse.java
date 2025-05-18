package com.member.dto.response;

import com.config.jwt.TokenWithExpiration;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final TokenWithExpiration accessToken;
    private final TokenWithExpiration refreshToken;

    @Builder
    public LoginResponse(TokenWithExpiration accessToken, TokenWithExpiration refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
