package com.sloth.plan_puzzle.domain.vote;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_TIMESLOT;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.VoteJpaEntity;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record Vote(
        Long id,
        LocalDateTime deadline
) {
    public static Vote fromEntity(final VoteJpaEntity voteEntity) {
        return Vote.builder()
                .id(voteEntity.getId())
                .deadline(voteEntity.getDeadline())
                .build();
    }

    public VoteJpaEntity toEntity(final RecruitmentJpaEntity recruitmentEntity) {
        return VoteJpaEntity.create(deadline, recruitmentEntity);
    }

    public Vote validate() {
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new CustomException(INVALID_TIMESLOT);
        }
        return this;
    }
}
