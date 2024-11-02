package com.sloth.plan_puzzle.persistence.repository.user;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.NOT_FOUND_USER;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private UserJpaEntity createUser() {
        return UserJpaEntity.builder()
                .loginId("loginId")
                .loginPw("password")
                .name("test")
                .email("test@ajou.ac.kr")
                .gender(Gender.FEMALE)
                .ageGroup(AgeGroup.TWENTIES)
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("유저를 loginId로 찾을 수 있다.")
    void getByLoginIdTest(){
        //given
        UserJpaEntity userJpaEntity = createUser();
        //when
        userRepository.save(userJpaEntity);
        //then
        assertThatNoException().isThrownBy(()->userRepository.getByLoginId("loginId"));
    }

    @Test
    @DisplayName("유저를 잘못된 loginId로 찾으면 에러가 발생한다.")
    void getByLoginIdFailTest(){
        //given
        UserJpaEntity userJpaEntity = createUser();
        //when
        userRepository.save(userJpaEntity);
        //then
        assertThatThrownBy(() -> userRepository.getByLoginId("loginId_fail"))
                .hasMessage(NOT_FOUND_USER.getMessage());
    }
}
