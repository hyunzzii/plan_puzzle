package com.sloth.plan_puzzle.domain.schedule.user;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_SCHEDULE_TIME;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.schedule.user.UserScheduleJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserSchedule(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String title,
        String content,
        UserScheduleState state
) {

    public static UserSchedule fromEntity(final UserScheduleJpaEntity scheduleEntity) {
        return UserSchedule.builder()
                .id(scheduleEntity.getId())
                .startDateTime(scheduleEntity.getStartDateTime())
                .endDateTime(scheduleEntity.getEndDateTime())
                .title(scheduleEntity.getTitle())
                .content(scheduleEntity.getContent())
                .state(scheduleEntity.getState())
                .build();
    }

    public UserScheduleJpaEntity toEntity(final UserJpaEntity userEntity) {
        return UserScheduleJpaEntity.create(startDateTime, endDateTime, title, content, state, userEntity);
    }

    public UserSchedule validateSchedule() {
        if (startDateTime.isAfter(endDateTime)) {
            throw new CustomException(INVALID_SCHEDULE_TIME);
        }
        return this;
    }
}
