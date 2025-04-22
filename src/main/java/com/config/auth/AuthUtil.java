package com.config.auth;


import static com.config.auth.AuthConstants.AUTHENTICATED_USER;

import com.exception.custom.ServerException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class AuthUtil {

    public void saveAuthenticatedMember(String email) {
        RequestAttributes requestAttributes = getRequestAttributes();
        requestAttributes.setAttribute(AUTHENTICATED_USER, email, RequestAttributes.SCOPE_REQUEST);
    }

    public String getMemberEmail() {
        RequestAttributes requestAttributes = getRequestAttributes();
        return (String) requestAttributes.getAttribute(AUTHENTICATED_USER, RequestAttributes.SCOPE_REQUEST);
    }

    private RequestAttributes getRequestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw ServerException.getInstance();
        }
        return requestAttributes;
    }

    public boolean isAuthenticated() {
        return getMemberEmail() != null;
    }
}
