package com.sloth.plan_puzzle.dto.vote;

import com.sloth.plan_puzzle.domain.vote.TimeSlot;
import com.sloth.plan_puzzle.domain.vote.Vote;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateVoteRequest(
        LocalDateTime deadline,
        List<CreateTimeSlotRequest> createTimeSlotList
) {
    public Vote toVote() {
        return Vote.builder()
                .deadline(deadline)
                .build();
    }

    public List<TimeSlot> toTimeSlotList() {
        return createTimeSlotList.stream()
                .map(CreateTimeSlotRequest::toTimeSlot).toList();
    }
}
