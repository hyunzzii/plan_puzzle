package com.sloth.plan_puzzle.dto.schdule.user;

import com.sloth.plan_puzzle.domain.schedule.user.UserSchedule;
import lombok.Builder;

@Builder
public record UserScheduleIdResponse(
        Long id
) {
    public static UserScheduleIdResponse fromDomain(final UserSchedule schedule) {
        return UserScheduleIdResponse.builder()
                .id(schedule.id())
                .build();
    }
}
