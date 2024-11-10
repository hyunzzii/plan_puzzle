package com.sloth.plan_puzzle.dto.recruitment;

import com.sloth.plan_puzzle.domain.recruitment.RecruitState;
import com.sloth.plan_puzzle.domain.recruitment.Recruitment;
import com.sloth.plan_puzzle.persistence.entity.recruitment.Region;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateRecruitmentRequest(
        @NotBlank
        String title,
        @NotBlank
        String content,
        @NotNull
        Integer recruitCapacity,
        @NotNull
        RecruitState recruitState,
        @NotBlank
        String imgUrl,
        @NotNull
        Region region,
        @NotNull
        Long authorId
) {
    public Recruitment toDomain() {
        return Recruitment.builder()
                .title(title)
                .content(content)
                .recruitCapacity(recruitCapacity)
                .recruitState(recruitState)
                .imgUrl(imgUrl)
                .region(region)
                .authorId(authorId)
                .build();
    }
}
