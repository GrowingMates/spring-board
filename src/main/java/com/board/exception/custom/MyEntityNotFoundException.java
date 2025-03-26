package com.board.exception.custom;

import com.board.exception.CustomException;
import com.board.exception.ErrorCodeType;
import java.util.Map;
import lombok.Getter;

@Getter
public class MyEntityNotFoundException extends CustomException {
    private final long entityId;

    private MyEntityNotFoundException(long entityId) {
        super(ErrorCodeType.ENTITY_NOT_FOUND);
        this.entityId = entityId;
    }

    @Override
    public Map<String, Object> getAdditionalDetails() {
        return Map.of("entityId", entityId);
    }

    public static MyEntityNotFoundException from(long entityId) {
        return new MyEntityNotFoundException(entityId);
    }
}
