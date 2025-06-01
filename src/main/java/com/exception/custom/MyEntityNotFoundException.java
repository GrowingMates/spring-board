package com.exception.custom;

import com.exception.CustomException;
import com.exception.ErrorCodeType;
import lombok.Getter;

import java.util.Map;

@Getter
public class MyEntityNotFoundException extends CustomException {
    private final Long entityId;

    private MyEntityNotFoundException(Long entityId) {
        super(ErrorCodeType.ENTITY_NOT_FOUND);
        this.entityId = entityId;
    }

    @Override
    public Map<String, Object> getAdditionalDetails() {
        return Map.of("entityId", entityId);
    }

    public static MyEntityNotFoundException from(Long entityId) {
        return new MyEntityNotFoundException(entityId);
    }
}
