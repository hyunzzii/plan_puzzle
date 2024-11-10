package com.sloth.plan_puzzle.domain.recruitment;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_SCHEDULE_TIME;

import com.sloth.plan_puzzle.common.validator.TimeValidator;
import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.domain.schedule.UserSchedule;
import com.sloth.plan_puzzle.domain.schedule.UserScheduleState;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentScheduleJpaEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RecruitmentSchedule(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String title,
        String content
) {
    public static List<RecruitmentSchedule> fromEntityList(
            final List<RecruitmentScheduleJpaEntity> scheduleEntityList) {
        return scheduleEntityList.stream()
                .map(RecruitmentSchedule::fromEntity)
                .toList();
    }

    public static RecruitmentSchedule fromEntity(final RecruitmentScheduleJpaEntity scheduleEntity) {
        return RecruitmentSchedule.builder()
                .id(scheduleEntity.getId())
                .startDateTime(scheduleEntity.getStartDateTime())
                .endDateTime(scheduleEntity.getEndDateTime())
                .title(scheduleEntity.getTitle())
                .content(scheduleEntity.getContent())
                .build();
    }

    public RecruitmentScheduleJpaEntity toEntity(final RecruitmentJpaEntity recruitmentEntity) {
        return RecruitmentScheduleJpaEntity.create(
                startDateTime, endDateTime, title, content, recruitmentEntity);
    }

    public UserSchedule toUserSchedule() {
        return UserSchedule.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .title(title)
                .content(content)
                .state(UserScheduleState.CANDIDATE)
                .build();
    }

    public RecruitmentSchedule validateSchedule() {
        TimeValidator.isValidateTimeFormat(startDateTime);
        TimeValidator.isValidateTimeFormat(endDateTime);

        if (startDateTime.isAfter(endDateTime)) {
            throw new CustomException(INVALID_SCHEDULE_TIME);
        }
        return this;
    }
}