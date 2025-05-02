package com.exception.custom;

import com.exception.CustomException;
import com.exception.ErrorCodeType;
import java.util.Map;
import lombok.Getter;

@Getter
public class LoginException extends CustomException {

    private final String errorMessage;

    private LoginException(String errorMessage) {
        super(ErrorCodeType.ENTITY_NOT_FOUND);
        this.errorMessage = errorMessage;
    }

    @Override
    public Map<String, Object> getAdditionalDetails() {
        return Map.of("errorMessage", errorMessage);
    }

    public static LoginException from(String errorMessage) {
        return new LoginException(errorMessage);
    }
}
