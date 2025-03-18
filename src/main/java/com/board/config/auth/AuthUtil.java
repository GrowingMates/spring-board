package com.board.config.auth;

import static com.board.config.jwt.JwtAuthFilter.AUTHENTICATED_USER;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class AuthUtil {
    public String getUser() {
        return (String) RequestContextHolder.getRequestAttributes()
                .getAttribute(AUTHENTICATED_USER, RequestAttributes.SCOPE_REQUEST);
    }

    public boolean isAuthenticated() {
        return getUser() != null;
    }
}
