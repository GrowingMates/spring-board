package com.exception.custom;

import com.exception.CustomException;
import com.exception.ErrorCodeType;
import java.util.Map;
import lombok.Getter;

@Getter
public class EmailNotFoundException extends CustomException {
    private final String email;

    private EmailNotFoundException(String email) {
        super(ErrorCodeType.ENTITY_NOT_FOUND);
        this.email = email;
    }

    @Override
    public Map<String, Object> getAdditionalDetails() {
        return Map.of("email", email);
    }

    public static EmailNotFoundException from(String email) {
        return new EmailNotFoundException(email);
    }
}
