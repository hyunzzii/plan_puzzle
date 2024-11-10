package com.sloth.plan_puzzle.dto.recruitment.response;

import com.sloth.plan_puzzle.domain.recruitment.RecruitState;
import com.sloth.plan_puzzle.persistence.entity.recruitment.Region;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import lombok.Builder;

@Builder
public record SimpleRecruitmentResponse (
        Long id,
        String title,
        String content,
        Integer recruitCapacity,
        Integer recruitCount,
        RecruitState recruitState,
        String imgUrl,
        Region region
) {
    public static SimpleRecruitmentResponse fromEntity(final RecruitmentJpaEntity recruitmentEntity) {
        return SimpleRecruitmentResponse.builder()
                .id(recruitmentEntity.getId())
                .title(recruitmentEntity.getTitle())
                .content(recruitmentEntity.getContent())
                .recruitCapacity(recruitmentEntity.getRecruitCapacity())
                .recruitCount(recruitmentEntity.getParticipationList().size())
                .recruitState(recruitmentEntity.getRecruitState())
                .imgUrl(recruitmentEntity.getImgUrl())
                .region(recruitmentEntity.getRegion())
                .build();
    }
}
