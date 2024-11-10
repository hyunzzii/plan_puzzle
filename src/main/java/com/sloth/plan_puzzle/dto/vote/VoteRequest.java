package com.sloth.plan_puzzle.dto.vote;

import java.util.List;

public record VoteRequest (
        List<Long> timeSlotIds
){
}
