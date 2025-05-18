package com.config.jwt.token;

import com.exception.custom.MyEntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private final Long TEST_MEMBER_ID = 1L;
    private final String TEST_TOKEN = "testRefreshToken";
    private final RefreshToken TEST_REFRESH_TOKEN = new RefreshToken(TEST_MEMBER_ID, TEST_TOKEN);

    @Test
    @DisplayName("mergeToken - 기존 토큰이 없을 때 저장")
    void mergeToken_새로운토큰저장() {
        // given
        given(refreshTokenRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.empty());
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(TEST_REFRESH_TOKEN);

        // when
        refreshTokenService.mergeToken(TEST_MEMBER_ID, TEST_TOKEN);

        // then
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test()
    @DisplayName("mergeToken - 기존 토큰이 있을 때 업데이트")
    void mergeToken_기존토큰업데이트() {
        // given
        String newRefreshToken = "newRefreshToken";
        RefreshToken existingRefreshToken = new RefreshToken(TEST_MEMBER_ID, "oldRefreshToken");
        given(refreshTokenRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(existingRefreshToken));

        // when
        refreshTokenService.mergeToken(TEST_MEMBER_ID, newRefreshToken);

        // then
        assertThat(existingRefreshToken.getToken()).isEqualTo(newRefreshToken);
        verify(refreshTokenRepository, times(1)).findById(TEST_MEMBER_ID);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("isTokenValid - 토큰이 유효할 때 true 반환")
    void isTokenValid_유효한토큰() {
        // given
        given(refreshTokenRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(TEST_REFRESH_TOKEN));

        // when
        boolean isValid = refreshTokenService.isTokenValid(TEST_MEMBER_ID, TEST_TOKEN);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid - 토큰이 유효하지 않을 때 false 반환")
    void isTokenValid_유효하지않은토큰() {
        // given
        given(refreshTokenRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(TEST_REFRESH_TOKEN));
        String invalidToken = "invalidRefreshToken";

        // when
        boolean isValid = refreshTokenService.isTokenValid(TEST_MEMBER_ID, invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - 해당 멤버 ID로 저장된 토큰이 없을 때 false 반환")
    void isTokenValid_토큰없음() {
        // given
        given(refreshTokenRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.empty());

        // when
        boolean isValid = refreshTokenService.isTokenValid(TEST_MEMBER_ID, TEST_TOKEN);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("deleteToken - 멤버 ID로 토큰 삭제")
    void deleteToken_토큰삭제() {
        // when
        refreshTokenService.deleteToken(TEST_MEMBER_ID);

        // then
        verify(refreshTokenRepository, times(1)).deleteById(TEST_MEMBER_ID);
    }

    @Test
    @DisplayName("findByMemberId - 멤버 ID로 토큰 찾기 - 존재할 때")
    void findByMemberId_토큰존재() {
        // given
        given(refreshTokenRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(TEST_REFRESH_TOKEN));

        // when
        RefreshToken foundToken = refreshTokenService.findByMemberId(TEST_MEMBER_ID);

        // then
        assertThat(foundToken).isEqualTo(TEST_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("findByMemberId - 멤버 ID로 토큰 찾기 - 존재하지 않을 때 MyEntityNotFoundException 발생")
    void findByMemberId_토큰없음() {
        // given
        given(refreshTokenRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> refreshTokenService.findByMemberId(TEST_MEMBER_ID))
                .isInstanceOf(MyEntityNotFoundException.class);
    }
}
