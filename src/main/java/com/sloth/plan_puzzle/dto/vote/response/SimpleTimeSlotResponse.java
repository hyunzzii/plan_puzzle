package com.sloth.plan_puzzle.dto.vote.response;

import com.sloth.plan_puzzle.domain.vote.TimeSlot;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record SimpleTimeSlotResponse(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public static List<SimpleTimeSlotResponse> fromDomainList(final List<TimeSlot> timeSlots) {
        return timeSlots.stream()
                .map(SimpleTimeSlotResponse::fromDomain)
                .toList();
    }

    public static SimpleTimeSlotResponse fromDomain(final TimeSlot timeSlot) {
        return SimpleTimeSlotResponse.builder()
                .startDateTime(timeSlot.startDateTime())
                .endDateTime(timeSlot.endDateTime())
                .build();
    }
}
