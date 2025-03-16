package com.board.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties("jwt")
@AllArgsConstructor
public class JwtProperties {

    private String issuer;
    private String secretKey;
}
