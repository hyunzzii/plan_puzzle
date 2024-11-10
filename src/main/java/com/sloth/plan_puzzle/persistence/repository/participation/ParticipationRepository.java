package com.sloth.plan_puzzle.persistence.repository.participation;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.PARTICIPATION_NOT_EXIST;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.UNAUTHORIZED_ACCESS;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.participation.ParticipationJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipationRepository extends JpaRepository<ParticipationJpaEntity, Long> {
    @Query("SELECT p FROM ParticipationJpaEntity p WHERE p.recruitment.id = :recruitmentId")
    List<ParticipationJpaEntity> findByRecruitmentId(@Param("recruitmentId") Long recruitmentId);

    @Query("SELECT p FROM ParticipationJpaEntity p WHERE p.recruitment.id = :recruitmentId AND p.channel.id = :channelId")
    Optional<ParticipationJpaEntity> findByRecruitmentIdAndChannelId(@Param("recruitmentId") Long recruitmentId,
                                                                     @Param("channelId") Long channelId);


    default void deleteParticipationByRecruitmentIdAndChannelId(final Long recruitmentId, final Long channelId) {
        ParticipationJpaEntity participationEntity = findByRecruitmentIdAndChannelId(recruitmentId, channelId)
                .orElseThrow(() -> new CustomException(PARTICIPATION_NOT_EXIST));
        delete(participationEntity);
    }

    default ChannelJpaEntity getChannelByParticipation(final Long recruitmentId, final Long channelId) {
        return findByRecruitmentIdAndChannelId(recruitmentId, channelId)
                .map(ParticipationJpaEntity::getChannel)
                .orElseThrow(() -> new CustomException(UNAUTHORIZED_ACCESS));
    }
}
