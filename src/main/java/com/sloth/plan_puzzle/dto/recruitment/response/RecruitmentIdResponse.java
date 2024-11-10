package com.sloth.plan_puzzle.dto.recruitment.response;

import com.sloth.plan_puzzle.domain.recruitment.Recruitment;
import lombok.Builder;

@Builder
public record RecruitmentIdResponse (
        Long recruitmentId
){
    public static RecruitmentIdResponse fromDomain(final Recruitment recruitment){
        return RecruitmentIdResponse.builder()
                .recruitmentId(recruitment.id())
                .build();
    }
}
