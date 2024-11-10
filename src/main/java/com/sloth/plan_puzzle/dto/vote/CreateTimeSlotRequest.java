package com.sloth.plan_puzzle.dto.vote;

import com.sloth.plan_puzzle.domain.vote.TimeSlot;
import com.sloth.plan_puzzle.domain.vote.Vote;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateTimeSlotRequest(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public TimeSlot toTimeSlot(){
        return TimeSlot.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }
}
