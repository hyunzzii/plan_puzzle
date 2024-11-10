package com.sloth.plan_puzzle.dto.recruitment.response;

import com.sloth.plan_puzzle.domain.recruitment.RecruitmentSchedule;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentScheduleJpaEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RecruitmentScheduleResponse (
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String title,
        String content
){
    public static List<RecruitmentScheduleResponse> fromEntityList(final List<RecruitmentScheduleJpaEntity> schedules){
        return schedules.stream()
                .map(RecruitmentScheduleResponse::fromEntity)
                .toList();
    }
    public static RecruitmentScheduleResponse fromEntity(final RecruitmentScheduleJpaEntity schedule){
        return RecruitmentScheduleResponse.builder()
                .id(schedule.getId())
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .build();
    }
}
