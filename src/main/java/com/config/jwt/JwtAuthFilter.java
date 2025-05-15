package com.config.jwt;

import com.config.auth.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthUtil authUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        String path = request.getRequestURI();

        if (!AuthRequiredPath.isAuthRequired(method, path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.extractFromHeader(request);
        if (token != null && jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractEmail(token);
            authUtil.saveAuthenticatedMember(email);
            filterChain.doFilter(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - jwt 인증 실패!!!");
    }
}
