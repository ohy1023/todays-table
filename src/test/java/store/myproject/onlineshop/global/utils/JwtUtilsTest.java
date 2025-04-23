package store.myproject.onlineshop.global.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtUtilsTest {

    JwtUtils jwtUtils;

    String secretKey = "a2F2eWlqYWZhc2xqZmt1aGtnc3Zkc2Zhc2Rxa3Zhc2Zhc2ZkcWtoc2ZkazEyMzQ1Ng=="; // 256비트 base64
    Long accessTokenExpiration = 1000L * 60 * 60; // 1시간
    Long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 14; // 14일

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtUtils, "accessTokenExpiration", accessTokenExpiration);
        ReflectionTestUtils.setField(jwtUtils, "refreshTokenExpiration", refreshTokenExpiration);
    }

    @Test
    @DisplayName("AccessToken을 생성할 수 있다")
    void create_access_token() {
        // when
        String token = jwtUtils.createAccessToken("test@email.com");

        // then
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("RefreshToken을 생성할 수 있다")
    void create_refresh_token() {
        // when
        String token = jwtUtils.createRefreshToken("test@email.com");

        // then
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("토큰에서 이메일을 추출할 수 있다")
    void get_email_from_token() {
        // given
        String token = jwtUtils.createAccessToken("test@email.com");

        // when
        String email = jwtUtils.getEmail(token);

        // then
        assertThat(email).isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("토큰이 만료되지 않았음을 확인할 수 있다")
    void is_not_expired_token() {
        // given
        String token = jwtUtils.createAccessToken("test@email.com");

        // when
        boolean expired = jwtUtils.isExpired(token);

        // then
        assertThat(expired).isFalse();
    }

    @Test
    @DisplayName("토큰 만료 시간(남은 시간)을 가져올 수 있다")
    void get_expiration_time() {
        // given
        String token = jwtUtils.createAccessToken("test@email.com");

        // when
        Long remaining = jwtUtils.getExpiration(token);

        // then
        assertThat(remaining).isPositive();
    }

    @Test
    @DisplayName("토큰에서 Claims를 추출할 수 있다")
    void extract_claims_from_token() {
        // given
        String token = jwtUtils.createAccessToken("test@email.com");

        // when
        Claims claims = jwtUtils.extractClaims(token);

        // then
        assertThat(claims.get("email")).isEqualTo("test@email.com");
    }

    @Test
    @DisplayName("isValid는 서명된 토큰에 대해 false를 반환한다 (isSigned)")
    void is_valid_returns_false_for_signed_token() {
        // given
        String token = jwtUtils.createAccessToken("test@email.com");

        // when
        boolean result = jwtUtils.isValid(token);

        // then
        assertThat(result).isFalse(); // isSigned(token) => true → not → false
    }

    @Test
    @DisplayName("잘못된 토큰이면 예외가 발생한다")
    void invalid_token_throws_exception() {
        // given
        String invalidToken = "invalid.token.value";

        // then
        assertThatThrownBy(() -> jwtUtils.extractClaims(invalidToken))
                .isInstanceOf(JwtException.class);
    }
}
