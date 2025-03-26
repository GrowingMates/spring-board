package com.board.exception.custom;

import com.board.exception.CustomException;
import com.board.exception.ErrorCodeType;
import java.util.Map;
import lombok.Getter;

@Getter
public class DifferentOwnerException extends CustomException {
    private final String email;

    private DifferentOwnerException(String email) {
        super(ErrorCodeType.AUTHORIZATION_ERROR);
        this.email = email;
    }

    @Override
    public Map<String, Object> getAdditionalDetails() {
        return Map.of("ownerEmail", email);
    }

    public static DifferentOwnerException from(String ownerEmail) {
        return new DifferentOwnerException(ownerEmail);
    }
}
