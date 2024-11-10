package com.sloth.plan_puzzle.domain.recruitment;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_RECRUIT_CAPACITY;

import com.sloth.plan_puzzle.common.validator.UrlValidator;
import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.Region;
import lombok.Builder;

@Builder
public record Recruitment(
        Long id,
        String title,
        String content,
        Integer recruitCapacity,
        Integer recruitCount,
        RecruitState recruitState,
        String imgUrl,
        Region region,
        Long authorId
) {
    public static Recruitment fromEntity(final RecruitmentJpaEntity recruitmentEntity) {
        return Recruitment.builder()
                .id(recruitmentEntity.getId())
                .title(recruitmentEntity.getTitle())
                .content(recruitmentEntity.getContent())
                .recruitCapacity(recruitmentEntity.getRecruitCapacity())
                .recruitCount(recruitmentEntity.getParticipationList().size())
                .recruitState(recruitmentEntity.getRecruitState())
                .imgUrl(recruitmentEntity.getImgUrl())
                .region(recruitmentEntity.getRegion())
                .authorId(recruitmentEntity.getAuthor().getId())
                .build();
    }


    public Recruitment validateRecruitCapacityForUpdate(final Integer currentParticipationSize) {
        if (this.recruitCapacity < currentParticipationSize) {
            throw new CustomException(INVALID_RECRUIT_CAPACITY);
        }
        return this;
    }

    public Recruitment validateImgUrl() {
        UrlValidator.isValidateUrl(imgUrl);
        return this;
    }

    public RecruitmentJpaEntity toEntity(final ChannelJpaEntity channelEntity) {
        return RecruitmentJpaEntity.create(title, content, recruitCapacity, recruitState, imgUrl,
                region, channelEntity);
    }
}
