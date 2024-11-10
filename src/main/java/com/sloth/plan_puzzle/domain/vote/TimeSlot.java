package com.sloth.plan_puzzle.domain.vote;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_TIMESLOT;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.vote.TimeSlotJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.VoteJpaEntity;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TimeSlot(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public static TimeSlot fromEntity(final TimeSlotJpaEntity timeSlotEntity) {
        return TimeSlot.builder()
                .id(timeSlotEntity.getId())
                .startDateTime(timeSlotEntity.getStartDateTime())
                .endDateTime(timeSlotEntity.getEndDateTime())
                .build();
    }

    public TimeSlotJpaEntity toEntity(final VoteJpaEntity voteEntity) {
        return TimeSlotJpaEntity.create(startDateTime, endDateTime, voteEntity);
    }

    public static Integer DURATION_LIMIT = 7;

    public static TimeSlot create(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        return TimeSlot.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

    public TimeSlot validate() {
        if (startDateTime.isAfter(endDateTime)) {
            throw new CustomException(INVALID_TIMESLOT);
        }
        return this;
    }

}
