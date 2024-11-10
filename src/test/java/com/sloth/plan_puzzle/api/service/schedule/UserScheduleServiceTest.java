package com.sloth.plan_puzzle.api.service.schedule;

import static com.sloth.plan_puzzle.domain.schedule.UserScheduleState.CONFIRMED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sloth.plan_puzzle.persistence.entity.schedule.UserScheduleJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.schedule.UserScheduleRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserScheduleServiceTest {

    @InjectMocks
    private UserScheduleService userScheduleService;

    @Mock
    private UserScheduleRepository userScheduleRepository;


    private final Long userId = 1L;
    private List<UserScheduleJpaEntity> existingSchedules;

    @BeforeEach
    void setUp() {
        List<LocalDateTime> startDateTimeList = List.of(LocalDateTime.parse("2024-11-11T10:00"),
                LocalDateTime.parse("2024-11-11T13:00"), LocalDateTime.parse("2024-11-11T20:00")
        );
        List<LocalDateTime> endDateTimeList = List.of(LocalDateTime.parse("2024-11-11T12:30"),
                LocalDateTime.parse("2024-11-11T18:00"), LocalDateTime.parse("2024-11-11T22:30")
        );
        existingSchedules = new ArrayList<>();
        for (int i = 0; i < startDateTimeList.size(); i++) {
            existingSchedules.add(createSchedule(startDateTimeList.get(i), endDateTimeList.get(i)));
        }
    }

    @DisplayName("user의 비어있는 시간을 조회합니다.")
    @Test
    void getAvailableTimesTest() {
        //given
        when(userScheduleRepository.findByPeriodAndUserId(
                any(), any(), any(), any()))
                .thenReturn(existingSchedules);
        Integer duration = 60;
        //when
        //then
        assertThat(userScheduleService.getAvailableTimes(
                LocalDateTime.parse("2024-11-11T07:00"),
                LocalDateTime.parse("2024-11-12T00:00"),
                duration, userId))
                .hasSize(17);

    }

    private UserScheduleJpaEntity createSchedule(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return UserScheduleJpaEntity.create(startDateTime, endDateTime, "title", "?",
                CONFIRMED, null);
    }
}