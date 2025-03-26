package com.board.exception.custom;

import com.board.exception.CustomException;
import com.board.exception.ErrorCodeType;
import java.util.Map;
import lombok.Getter;

@Getter
public class SignUpException extends CustomException {
    private final String errorMessage;

    private SignUpException(String errorMessage) {
        super(ErrorCodeType.DUPLICATE);
        this.errorMessage = errorMessage;
    }

    @Override
    public Map<String, Object> getAdditionalDetails() {
        return Map.of("errorMessage", errorMessage);
    }

    public static SignUpException from(String errorMessage) {
        return new SignUpException(errorMessage);
    }
}
