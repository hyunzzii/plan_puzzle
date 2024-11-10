package com.sloth.plan_puzzle.api.service.participation;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.*;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.participation.ParticipationJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.participation.ParticipationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;

    public void participate(final RecruitmentJpaEntity recruitmentEntity, final ChannelJpaEntity channelEntity) {
        if (recruitmentEntity.getRecruitCapacity() <= recruitmentEntity.getParticipationList().size()) {
            throw new CustomException(ALREADY_FILLED);
        }
        participationRepository.save(ParticipationJpaEntity.create(recruitmentEntity, channelEntity));
    }

    public void withdraw(final Long recruitmentId, final Long channelId) {
        participationRepository.deleteParticipationByRecruitmentIdAndChannelId(recruitmentId, channelId);
    }

    public List<Channel> getChannelsByParticipation(final Long recruitmentId) {
        return participationRepository.findByRecruitmentId(recruitmentId).stream()
                .map(participation -> Channel.fromEntity(participation.getChannel()))
                .toList();
    }

    public void verifyParticipation(final Long recruitmentId, final Long channelId) {
        boolean isParticipant = participationRepository.findByRecruitmentId(recruitmentId)
                .stream()
                .anyMatch(participation -> participation.getChannel().getId().equals(channelId));

        if (!isParticipant) {
            throw new CustomException(CustomExceptionInfo.UNAUTHORIZED_ACCESS);
        }
    }

    public ChannelJpaEntity getChannelEntity(final Long recruitmentId, final Long channelId) {
        return participationRepository.getChannelByParticipation(recruitmentId, channelId);
    }

}
