package com.sloth.plan_puzzle.dto.schedule.user;

import com.sloth.plan_puzzle.domain.schedule.user.UserSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdateUserScheduleRequest(
        @NotNull
        LocalDateTime startDateTime,

        @NotNull
        LocalDateTime endDateTime,

        @NotBlank
        String title,

        String content
) {
    public UserSchedule toDomain() {
        return UserSchedule.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .title(title)
                .content(content)
                .build();
    }
}
