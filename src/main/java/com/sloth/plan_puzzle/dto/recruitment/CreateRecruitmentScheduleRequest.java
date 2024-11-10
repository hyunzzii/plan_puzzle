package com.sloth.plan_puzzle.dto.recruitment;

import com.sloth.plan_puzzle.domain.recruitment.RecruitmentSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateRecruitmentScheduleRequest(
        @NotNull
        LocalDateTime startDateTime,

        @NotNull
        LocalDateTime endDateTime,

        @NotBlank
        String title,

        String content
) {
    public RecruitmentSchedule toDomain() {
        return RecruitmentSchedule.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .title(title)
                .content(content)
                .build();
    }
}
