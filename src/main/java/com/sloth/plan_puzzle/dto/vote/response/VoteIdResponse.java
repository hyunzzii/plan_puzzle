package com.sloth.plan_puzzle.dto.vote.response;


import com.sloth.plan_puzzle.domain.vote.Vote;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VoteIdResponse(
        Long voteId,
        LocalDateTime daedline
) {

    public static VoteIdResponse fromDomain(final Vote vote) {
        return VoteIdResponse.builder()
                .voteId(vote.id())
                .daedline(vote.deadline())
                .build();
    }
}
