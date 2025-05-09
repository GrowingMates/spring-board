package com.config.jwt;

import com.config.auth.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthUtil authUtil;

    private static final Map<String, Set<String>> AUTH_REQUIRED_PATH = Map.of(
            "POST", Set.of("/articles", "/comments", "/members/logout"),
            "PUT", Set.of("/articles/*", "/comments/*"),
            "DELETE", Set.of("/articles/*", "/comments/*", "/members/withdraw"),
            "PATCH", Set.of("/articles/*", "/comments/*")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod().toUpperCase();
        String path = request.getRequestURI();

        if (!isRequireAuth(method, path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.extractToken(request);
        if (token != null && jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractEmail(token);
            authUtil.saveAuthenticatedMember(email);
            filterChain.doFilter(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - jwt 인증 실패!!!");
    }

    private boolean isRequireAuth(String method, String path) {
        Set<String> authRequiredPaths = AUTH_REQUIRED_PATH.get(method);
        if (authRequiredPaths == null) return false;

        for (String authPath : authRequiredPaths) {
            if (authPath.endsWith("/*")) {
                String base = authPath.substring(0, authPath.length() - 2);
                if (path.startsWith(base + "/")) {
                    return true;
                }
            } else if (path.equals(authPath)) {
                return true;
            }
        }

        return false;
    }
}
