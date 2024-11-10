package com.sloth.plan_puzzle.domain.vote;

import static com.sloth.plan_puzzle.common.TimeValidator.TimeFormatUnit;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_DURATION;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_TIMESLOT;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.vote.TimeSlotJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.VoteJpaEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record TimeSlot(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public static TimeSlot fromEntity(final TimeSlotJpaEntity timeSlotEntity){
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

//    public static List<TimeSlot> generateTimeSlots(final LocalDate startDate, final LocalDate endDate,
//                                                   final Integer durationMinutes) {
//        List<TimeSlot> timeSlots = new ArrayList<>();
//        if (endDate.isAfter(startDate.plusDays(DURATION_LIMIT)) || durationMinutes % 5 != 0) {
//            throw new CustomException(INVALID_DURATION);
//        }
//        LocalDateTime dateTime = startDate.atStartOfDay();
//        while (!dateTime.isAfter(endDate.atStartOfDay())) {
//            timeSlots.add(TimeSlot.create(dateTime, dateTime.plusMinutes(durationMinutes)));
//            dateTime = dateTime.plusMinutes(TimeFormatUnit);
//        }
//        return timeSlots;
//    }

    public TimeSlot validate() {
        if (startDateTime.isAfter(endDateTime)) {
            throw new CustomException(INVALID_TIMESLOT);
        }
        return this;
    }

}
