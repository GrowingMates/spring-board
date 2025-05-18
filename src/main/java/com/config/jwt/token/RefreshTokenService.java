package com.config.jwt.token;

import com.exception.custom.MyEntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void mergeToken(Long memberId, String token) {
        RefreshToken refreshToken = refreshTokenRepository.findById(memberId)
                .orElse(null);
        if (refreshToken != null) {
            refreshToken.update(token);
            return;
        }
        refreshTokenRepository.save(new RefreshToken(memberId, token));
    }

    public boolean isTokenValid(Long memberId, String token) {
        return refreshTokenRepository.findById(memberId)
                .map(saved -> saved.getToken().equals(token))
                .orElse(false);
    }

    @Transactional
    public void deleteToken(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }

    public RefreshToken findByMemberId(Long memberId) {
        return refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> MyEntityNotFoundException.from(memberId));
    }
}
