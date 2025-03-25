package com.board.config.auth;

import static com.board.config.jwt.JwtAuthFilter.AUTHENTICATED_USER;

import com.board.exception.custom.ServerException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class AuthUtil {
    public String getMemberEmail() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw ServerException.getInstance();
        }
        return (String) requestAttributes.getAttribute(AUTHENTICATED_USER, RequestAttributes.SCOPE_REQUEST);
    }

    public boolean isAuthenticated() {
        return getMemberEmail() != null;
    }
}
