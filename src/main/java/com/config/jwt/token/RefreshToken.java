package com.config.jwt.token;

import com.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "refreshToken")
@AllArgsConstructor
public class RefreshToken extends BaseEntity {

    @Id
    @Column(name = "member_id", updatable = false)
    private Long memberId;

    @Column(nullable = false)
    private String token;

    public void update(String newToken) {
        this.token = newToken;
    }
}
