package com.sloth.plan_puzzle.dto.schedule.user;

import com.sloth.plan_puzzle.domain.schedule.user.UserSchedule;
import com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateUserScheduleRequest(
        @NotNull
        LocalDateTime startDateTime,

        @NotNull
        LocalDateTime endDateTime,

        @NotBlank
        String title,

        @NotNull
        UserScheduleState state,

        String content
) {
    public UserSchedule toDomain() {
        return UserSchedule.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .title(title)
                .content(content)
                .state(state)
                .build();
    }
}
