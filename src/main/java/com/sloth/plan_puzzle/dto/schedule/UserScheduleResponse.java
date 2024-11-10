package com.sloth.plan_puzzle.dto.schedule;

import com.sloth.plan_puzzle.domain.schedule.UserSchedule;
import com.sloth.plan_puzzle.domain.schedule.UserScheduleState;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record UserScheduleResponse(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String title,
        String content,
        UserScheduleState state
) {
    public static List<UserScheduleResponse> fromDomainList(final List<UserSchedule> schedules){
        return schedules.stream().map(UserScheduleResponse::fromDomain).toList();
    }
    public static UserScheduleResponse fromDomain(final UserSchedule schedule){
        return UserScheduleResponse.builder()
                .id(schedule.id())
                .startDateTime(schedule.startDateTime())
                .endDateTime(schedule.endDateTime())
                .title(schedule.title())
                .content(schedule.content())
                .state(schedule.state())
                .build();
    }
}
