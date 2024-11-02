package com.sloth.plan_puzzle.api.service.auth;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_PASSWORD;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sloth.plan_puzzle.common.security.jwt.RedisUtil;
import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.dto.auth.LoginRequest;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@MockBean(RedisUtil.class)
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpUser(){
        UserJpaEntity userJpaEntity = UserJpaEntity.builder()
                .loginId("loginId")
                .loginPw(passwordEncoder.encode("password"))
                .name("test")
                .email("test@ajou.ac.kr")
                .gender(Gender.FEMALE)
                .ageGroup(AgeGroup.TWENTIES)
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(userJpaEntity);
    }

    @DisplayName("password 검증에 성공했습니다.")
    @Test
    void validateUserByPassword(){
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("loginId")
                .loginPw("password")
                .build();
        //when, then
        assertThatNoException().isThrownBy(()-> authService.validateUserByPassword(loginRequest));
    }

    @DisplayName("password 검증에 실패하면 에러가 발생합니다.")
    @Test
    void validateUserByPasswordFail(){
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .loginId("loginId")
                .loginPw("password_fail")
                .build();
        //when, then
        assertThatThrownBy(()-> authService.validateUserByPassword(loginRequest))
                .hasMessage(INVALID_PASSWORD.getMessage());
    }
}
