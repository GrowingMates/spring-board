package com.board.global.interceptor;

import com.board.global.interceptor.exception.NotFoundTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER_NAME = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenHeader = Optional.ofNullable(request.getHeader(TOKEN_HEADER_NAME))
                .orElseThrow(NotFoundTokenException::new);
        return tokenHeader.startsWith("Bearer ");
    }
}
