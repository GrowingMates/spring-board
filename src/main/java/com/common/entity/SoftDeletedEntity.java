package com.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class SoftDeletedEntity extends BaseEntity {

    @Column(nullable = false)
    protected boolean isDeleted = false;

    public void softDelete() {
        this.isDeleted = true;
    }
}
