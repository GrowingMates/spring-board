package com.board.global.interceptor;

import com.board.global.exception.GlobalErrorCode;
import com.board.global.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER_NAME = "Authorization";
    private static final String TOKEN_START_NAME = "Bearer ";
    private static final String ALLOW_HTTP_METHOD = "GET";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(isAllowHttpMethod(request)) {
            return true;
        }

        String tokenHeader = Optional.ofNullable(request.getHeader(TOKEN_HEADER_NAME))
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_TOKEN));

        return tokenHeader.startsWith(TOKEN_START_NAME);
    }

    private boolean isAllowHttpMethod(HttpServletRequest request) {
        return Objects.equals(request.getMethod(), ALLOW_HTTP_METHOD);
    }
}
