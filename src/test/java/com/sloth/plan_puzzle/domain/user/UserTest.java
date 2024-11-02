package com.sloth.plan_puzzle.domain.user;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_PASSWORD;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sloth.plan_puzzle.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


class UserTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @DisplayName("비밀번호가 일치하면 메서드를 통과합니다.")
    @Test
    void validatePasswordTest() {
        //given
        User user = createUser();

        //when, then
        assertThatNoException().isThrownBy(() ->
                user.validatePassword(passwordEncoder, "password"));
    }

    @DisplayName("비밀번호가 일치하지 않으면 에러를 반환합니다.")
    @Test
    void validatePasswordFailTest() {
        //given
        User user = createUser();

        //when, then
        assertThatThrownBy(() ->
                user.validatePassword(passwordEncoder, "password_fail"))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_PASSWORD.getMessage());
    }

    private User createUser() {
        return User.builder()
                .loginId("loginId")
                .loginPw(passwordEncoder.encode("password"))
                .build();
    }
}
