package com.config.jwt;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

@RequiredArgsConstructor
@Getter
public enum AuthRequiredPath {
    CREATE_ARTICLE("/articles", List.of(HttpMethod.POST)),
    UPDATE_ARTICLE("/articles/*", List.of(HttpMethod.PUT, HttpMethod.PATCH)),
    DELETE_ARTICLE("/articles/*", List.of(HttpMethod.DELETE)),

    CREATE_COMMENT("/articles/*/comments", List.of(HttpMethod.POST)),
    UPDATE_COMMENT("/articles/*/comments/*", List.of(HttpMethod.PUT, HttpMethod.PATCH)),
    DELETE_COMMENT("/articles/*/comments/*", List.of(HttpMethod.DELETE)),

    CREATE_MEMBER("/members", List.of(HttpMethod.POST)),
    DELETE_MEMBER("/members/*", List.of(HttpMethod.DELETE)),
    LOGOUT_MEMBER("/members/*", List.of(HttpMethod.POST));

    private final String pathPattern;
    private final List<HttpMethod> methods;

    public static boolean isAuthRequired(HttpMethod method, String path) {
        for (AuthRequiredPath arp : AuthRequiredPath.values()) {
            if (pathMatchesPattern(path, arp.getPathPattern()) && arp.getMethods().contains(method)) {
                return true;
            }
        }
        return false;
    }

    private static boolean pathMatchesPattern(String path, String pattern) {
        String regexPattern = pattern
                .replace(".", "\\.")
                .replace("/*", "/[^/]+")
                .replace("/**", "(/[^/]+)*");
        return path.matches("^" + regexPattern + "$");
    }
}
