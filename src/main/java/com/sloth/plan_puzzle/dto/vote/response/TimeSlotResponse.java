package com.sloth.plan_puzzle.dto.vote.response;

import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.domain.vote.TimeSlot;
import com.sloth.plan_puzzle.dto.channel.SimpleChannelResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record TimeSlotResponse(
        Long id,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        List<SimpleChannelResponse> voters
) {
    public static TimeSlotResponse fromDomain(final TimeSlot timeSlot, final List<Channel> voters) {
        return TimeSlotResponse.builder()
                .id(timeSlot.id())
                .startDateTime(timeSlot.startDateTime())
                .endDateTime(timeSlot.endDateTime())
                .voters(voters.stream().map(SimpleChannelResponse::fromDomain).toList())
                .build();
    }
}
