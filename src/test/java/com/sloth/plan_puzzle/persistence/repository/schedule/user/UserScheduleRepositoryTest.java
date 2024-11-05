package com.sloth.plan_puzzle.persistence.repository.schedule.user;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.*;
import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CANDIDATE;
import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CONFIRMED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState;
import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.schedule.user.UserScheduleJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class UserScheduleRepositoryTest {

    @Autowired
    private UserScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    private UserScheduleJpaEntity scheduleEntity;
    private UserJpaEntity userEntity;

    @BeforeEach
    void setUpEntity() {
        userEntity = saveUserEntity();

        scheduleEntity = saveScheduleEntity(userEntity);
        scheduleRepository.save(scheduleEntity);
    }

    @DisplayName("schedule을 id로 조회했을 때 실패하면 예외가 발생합니다")
    @Test
    void getByIdFailTest() {
        //when, then
        assertThatThrownBy(() -> scheduleRepository.getScheduleById(scheduleEntity.getId() + 1))
                .isInstanceOf(CustomException.class)
                .hasMessage(NOT_FOUND_SCHEDULE.getMessage());
    }

    @DisplayName("schedule의 주인을 확인하여 schedule을 가져옵니다.")
    @Test
    void validateOwnerWhenGetScheduleTest() {
        //given
        //when
        UserScheduleJpaEntity foundScheduleEntity = scheduleRepository.getScheduleByIdAndUserId(
                scheduleEntity.getId(), userEntity.getId());
        //then
        assertThat(foundScheduleEntity).isEqualTo(scheduleEntity);
    }

    @DisplayName("schedule의 주인이 아니면 예외가 발생합니다.")
    @Test
    void validateOwnerWhenGetScheduleFailTest() {
        //given
        //when, then
        assertThatThrownBy(
                () -> scheduleRepository.getScheduleByIdAndUserId(scheduleEntity.getId(), userEntity.getId() + 1))
                .isInstanceOf(CustomException.class)
                .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
    }

    @DisplayName("schedule 기간 조회가 잘 수행됩니다.")
    @Test
    void findByPeriodAndUserIdTest() {
        //given
        saveScheduleEntity(
                "점심 약속", "파스타와 뇨끼", CANDIDATE, "2024-10-29T01:30", "2024-10-29T18:30");
        saveScheduleEntity(
                "저녁 약속", "샤브샤브", CONFIRMED, "2024-10-27T23:30", "2024-10-28T01:30");
        saveScheduleEntity(
                "치킨 먹기", "황금올리브", CONFIRMED, "2024-10-28T23:00", "2024-10-29T00:30");
        saveScheduleEntity(
                "생명 퀴즈", "8장", CONFIRMED, "2024-10-28T13:00", "2024-10-28T13:30");

        //when
        LocalDateTime startOfPeriod = LocalDateTime.parse("2024-10-28T00:00");
        LocalDateTime endOfPeriod = LocalDateTime.parse("2024-10-29T01:30");

        List<UserScheduleJpaEntity> periodSchedules = scheduleRepository.findByPeriodAndUserId(
                startOfPeriod, endOfPeriod, CONFIRMED, userEntity.getId());
        //then
        assertThat(periodSchedules)
                .hasSize(3)
                .extracting("title", "content", "state")
                .containsExactlyInAnyOrder(
                        tuple("저녁 약속", "샤브샤브", CONFIRMED),
                        tuple("생명 퀴즈", "8장", CONFIRMED),
                        tuple("치킨 먹기", "황금올리브", CONFIRMED)
                );
    }

    private UserScheduleJpaEntity saveScheduleEntity(final UserJpaEntity userEntity) {
        return UserScheduleJpaEntity.builder()
                .startDateTime(LocalDateTime.parse("2024-10-25T13:00"))
                .endDateTime(LocalDateTime.parse("2024-10-25T13:30"))
                .title("수강신청")
                .content("졸업하자...")
                .state(UserScheduleState.CONFIRMED)
                .user(userEntity)
                .build();
    }

    private UserScheduleJpaEntity saveScheduleEntity(String title, String content, UserScheduleState state,
                                                     String startDateTime, String endDateTime) {
        return scheduleRepository.save(UserScheduleJpaEntity.builder()
                .title(title)
                .content(content)
                .state(state)
                .startDateTime(LocalDateTime.parse(startDateTime))
                .endDateTime(LocalDateTime.parse(endDateTime))
                .user(userEntity)
                .build());
    }


    private UserJpaEntity saveUserEntity() {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(UserJpaEntity.builder()
                .loginId("loginId")
                .loginPw(passwordEncoder.encode("password"))
                .name("test")
                .email("test@ajou.ac.kr")
                .gender(Gender.FEMALE)
                .ageGroup(AgeGroup.TWENTIES)
                .role(UserRole.ROLE_USER)
                .build());
    }

}