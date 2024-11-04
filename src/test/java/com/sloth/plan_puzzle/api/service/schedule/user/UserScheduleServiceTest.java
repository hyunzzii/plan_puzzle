package com.sloth.plan_puzzle.api.service.schedule.user;


import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.NOT_FOUND_SCHEDULE;
import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CANDIDATE;
import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CONFIRMED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.security.jwt.RedisUtil;
import com.sloth.plan_puzzle.domain.schedule.user.UserSchedule;
import com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState;
import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.dto.schdule.user.UserScheduleCreateRequest;
import com.sloth.plan_puzzle.dto.schdule.user.UserScheduleUpdateRequest;
import com.sloth.plan_puzzle.persistence.entity.schedule.user.UserScheduleJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.schedule.user.UserScheduleRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@MockBean(RedisUtil.class)
@Transactional
class UserScheduleServiceTest {
    @Autowired
    private UserScheduleService scheduleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserScheduleRepository scheduleRepository;

    private UserJpaEntity userEntity;

    @BeforeEach
    void setUpEntity() {
        userEntity = createUserEntity();
        userRepository.save(userEntity);
    }

    @DisplayName("schedule을 만들 수 있습니다.")
    @Test
    void createScheduleTest() {
        //given
        UserScheduleCreateRequest request = createScheduleRequest("수강신청", "졸업하자...");

        //when
        UserSchedule schedule = scheduleService.createSchedule(request, userEntity.getId());

        //then
        UserScheduleJpaEntity scheduleEntity = scheduleRepository.getScheduleById(schedule.id());
        assertThat(scheduleEntity)
                .extracting("title", "content")
                .containsExactly("수강신청", "졸업하자...");

    }

    @DisplayName("schedule 정보를 수정할 수 있습니다.")
    @Test
    public void updateSchedule() {
        //given
        UserScheduleJpaEntity scheduleEntity = saveScheduleEntity("점심 약속", "파스타와 뇨끼", CONFIRMED, "2024-10-25T13:00",
                "2024-10-25T13:30");
        UserScheduleUpdateRequest request = createUpdateScheduleRequest("점심 약속", "샤브샤브");

        //when
        scheduleService.updateSchedule(scheduleEntity.getId(), request, userEntity.getId());

        //then
        UserScheduleJpaEntity foundScheduleEntity = scheduleRepository.getScheduleById(scheduleEntity.getId());
        assertThat(foundScheduleEntity)
                .extracting("title", "content")
                .containsExactly("점심 약속", "샤브샤브");
    }

    @DisplayName("schedule을 삭제할 수 있습니다.")
    @Test
    public void deleteSchedule() {
        //given
        UserScheduleJpaEntity scheduleEntity = saveScheduleEntity("점심 약속", "파스타와 뇨끼", CONFIRMED, "2024-10-25T13:00",
                "2024-10-25T13:30");

        //when
        scheduleService.deleteSchedule(scheduleEntity.getId(), userEntity.getId());

        //then
        assertThatThrownBy(() -> scheduleRepository.getScheduleById(scheduleEntity.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(NOT_FOUND_SCHEDULE.getMessage());
    }

    @DisplayName("schedule 상태를 변경할 수 있습니다.")
    @Test
    public void updateScheduleStatus() {
        //given
        UserScheduleJpaEntity scheduleEntity = saveScheduleEntity(
                "점심 약속", "파스타와 뇨끼", CONFIRMED, "2024-10-25T13:00", "2024-10-25T13:30");

        //when
        scheduleService.updateScheduleStatus(scheduleEntity.getId(), userEntity.getId());

        //then
        UserScheduleJpaEntity foundScheduleEntity = scheduleRepository.getScheduleById(scheduleEntity.getId());
        assertThat(foundScheduleEntity.getState()).isEqualTo(CANDIDATE);
    }

    @DisplayName("schedule을 기간별로 조회할 수 있으며, 이는 상태가 CONFIRMED 입니다.")
    @Test
    public void getCandidateSchedules() {
        saveScheduleEntity(
                "점심 약속", "파스타와 뇨끼", CANDIDATE, "2024-10-25T13:00", "2024-10-25T13:30");
        saveScheduleEntity(
                "저녁 약속", "샤브샤브", CONFIRMED, "2024-10-25T13:00", "2024-10-25T13:30");
        saveScheduleEntity(
                "생명 퀴즈", "8장", CANDIDATE, "2024-10-25T13:00", "2024-10-25T13:30");

        //when
        List<UserSchedule> periodSchedules = scheduleService.getCandidateSchedules(userEntity.getId());

        //then
        assertThat(periodSchedules).hasSize(2)
                .extracting("title", "content", "state")
                .containsExactlyInAnyOrder(
                        tuple("점심 약속", "파스타와 뇨끼", CANDIDATE),
                        tuple("생명 퀴즈", "8장", CANDIDATE)
                );
    }


    @DisplayName("schedule들 중 CANDIDATE 상태인 것들을 조회할 수 있습니다.")
    @Test
    public void getSchedulesWithinPeriod() {
        saveScheduleEntity(
                "점심 약속", "파스타와 뇨끼", CANDIDATE, "2024-10-23T14:00", "2024-10-23T18:30");
        saveScheduleEntity(
                "저녁 약속", "샤브샤브", CONFIRMED, "2024-10-28T23:00", "2024-10-29T00:30");
        saveScheduleEntity(
                "생명 퀴즈", "8장", CONFIRMED, "2024-10-28T13:00", "2024-10-28T13:30");

        //when
        LocalDate startOfPeriod = LocalDate.parse("2024-10-28");
        LocalDate endOfPeriod = LocalDate.parse("2024-10-29");
        List<UserSchedule> candidateSchedules = scheduleService.getSchedulesWithinPeriod(
                startOfPeriod.atStartOfDay(), endOfPeriod.atStartOfDay(), userEntity.getId());

        //then
        assertThat(candidateSchedules).hasSize(2)
                .extracting("title", "content", "state")
                .containsExactlyInAnyOrder(
                        tuple("저녁 약속", "샤브샤브", CONFIRMED),
                        tuple("생명 퀴즈", "8장", CONFIRMED)
                );
    }


    //메서드
    private UserJpaEntity createUserEntity() {
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

    private UserScheduleCreateRequest createScheduleRequest(String title, String content) {
        return UserScheduleCreateRequest.builder()
                .startDateTime(LocalDateTime.parse("2024-10-25T13:00"))
                .endDateTime(LocalDateTime.parse("2024-10-25T13:30"))
                .title(title)
                .content(content)
                .state(CONFIRMED)
                .build();
    }

    private UserScheduleUpdateRequest createUpdateScheduleRequest(String title, String content) {
        return UserScheduleUpdateRequest.builder()
                .startDateTime(LocalDateTime.parse("2024-10-25T13:00"))
                .endDateTime(LocalDateTime.parse("2024-10-25T13:30"))
                .title(title)
                .content(content)
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
}