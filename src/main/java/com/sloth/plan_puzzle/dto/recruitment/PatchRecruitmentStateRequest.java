package com.sloth.plan_puzzle.dto.recruitment;

import com.sloth.plan_puzzle.domain.recruitment.RecruitState;
import jakarta.validation.constraints.NotNull;

public record PatchRecruitmentStateRequest(
        @NotNull
        RecruitState recruitState
) {
}
