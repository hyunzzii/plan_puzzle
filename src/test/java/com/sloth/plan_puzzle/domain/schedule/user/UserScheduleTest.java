package com.sloth.plan_puzzle.domain.schedule.user;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.*;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserScheduleTest {
    @DisplayName("시작시간과 끝나는 시간이 유효합니다.")
    @Test
    void validScheduleDateTimeTest() {
        //given
        UserSchedule userSchedule = createSchedule("2024-10-25T13:00", "2024-10-25T13:30");

        //when, then
        assertThatNoException().isThrownBy(userSchedule::validateSchedule);
    }

    @DisplayName("시작시간과 끝나는 시간이 유효하지 않으면 예외가 발생합니다.")
    @Test
    void invalidScheduleDateTime() {
        //given
        UserSchedule userSchedule = createSchedule("2024-10-25T13:00:00", "2024-10-25T12:00:00");

        //when, then
        assertThatThrownBy(userSchedule::validateSchedule)
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_SCHEDULE_TIME.getMessage());
    }

    private UserSchedule createSchedule(String start, String end) {
        return UserSchedule.builder()
                .startDateTime(LocalDateTime.parse(start))
                .endDateTime(LocalDateTime.parse(end))
                .title("수강신청")
                .state(UserScheduleState.CONFIRMED)
                .build();
    }
}