package com.board.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@WebFilter(urlPatterns = "/articles/*")
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHENTICATED_USER = "authenticatedUser";

    private static final String COOKIE_NAME = "token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("doFilterInternal 실행됨!!!!!!!!!!!!!!!!!!!");
        String token = getJwtInformation(request);
        if (token != null && jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractEmail(token);
            RequestContextHolder.getRequestAttributes()
                    .setAttribute(AUTHENTICATED_USER, email, RequestAttributes.SCOPE_REQUEST);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtInformation(HttpServletRequest request) {
        if (request.getCookies() != null) { // 쿠키에서 jwt 확인
            String cookieToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (cookieToken != null) {
                return cookieToken;
            }
        }

        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
