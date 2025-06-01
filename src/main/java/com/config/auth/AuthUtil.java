package com.config.auth;


import com.exception.custom.ServerException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static com.config.auth.AuthConstants.AUTHENTICATED_USER;

@Component
public class AuthUtil {

    public void saveAuthenticatedMember(Long memberId) {
        RequestAttributes requestAttributes = getRequestAttributes();
        requestAttributes.setAttribute(AUTHENTICATED_USER, memberId, RequestAttributes.SCOPE_REQUEST);
    }

    public Long getMemberId() {
        RequestAttributes requestAttributes = getRequestAttributes();
        return (Long) requestAttributes.getAttribute(AUTHENTICATED_USER, RequestAttributes.SCOPE_REQUEST);
    }

    private RequestAttributes getRequestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw ServerException.getInstance();
        }
        return requestAttributes;
    }
}
