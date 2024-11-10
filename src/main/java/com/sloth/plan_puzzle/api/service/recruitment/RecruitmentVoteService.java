package com.sloth.plan_puzzle.api.service.recruitment;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.UNAUTHORIZED_ACCESS;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.VOTE_ALREADY_EXISTS;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.domain.vote.TimeSlot;
import com.sloth.plan_puzzle.domain.vote.Vote;
import com.sloth.plan_puzzle.dto.vote.response.TimeSlotResponse;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.ChannelVoteJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.TimeSlotJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.VoteJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.vote.ChannelVoteRepository;
import com.sloth.plan_puzzle.persistence.repository.vote.TimeSlotRepository;
import com.sloth.plan_puzzle.persistence.repository.vote.VoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitmentVoteService {
    private final ChannelVoteRepository channelVoteRepository;
    private final VoteRepository voteRepository;
    private final TimeSlotRepository timeSlotRepository;

    public Vote createVote(final Long recruitmentId, final Vote vote, final List<TimeSlot> timeSlots,
                           final RecruitmentJpaEntity recruitmentEntity) {
        if (voteRepository.existsVoteByRecruitment(recruitmentId)) {
            throw new CustomException(VOTE_ALREADY_EXISTS);
        }
        final VoteJpaEntity voteEntity = voteRepository.save(vote.toEntity(recruitmentEntity));
        createTimeSlots(timeSlots, voteEntity);
        return Vote.fromEntity(voteEntity);
    }

    private void createTimeSlots(final List<TimeSlot> timeSlots, final VoteJpaEntity voteEntity) {
        timeSlots.forEach(timeSlot -> {
            TimeSlotJpaEntity timeSlotEntity = timeSlot.toEntity(voteEntity);
            timeSlotRepository.save(timeSlotEntity);
        });
    }

    public Vote getVote(final Long recruitmentId) {
        return Vote.fromEntity(voteRepository.getVoteByRecruitmentId(recruitmentId));
    }

    public List<TimeSlotResponse> getTimeSlotsAndVoters(final Long voteId) {
        return timeSlotRepository.findListByVoteId(voteId).stream()
                .map(TimeSlot::fromEntity)
                .map(this::toTimeSlotResponse)
                .toList();
    }

    private TimeSlotResponse toTimeSlotResponse(final TimeSlot timeSlot) {
        List<Channel> voters = channelVoteRepository.findListByTimeSlotId(timeSlot.id()).stream()
                .map(channelVote -> Channel.fromEntity(channelVote.getChannel()))
                .toList();
        return TimeSlotResponse.fromDomain(timeSlot, voters);
    }

    public void vote(final Long channelId, final Long voteId,
                     final List<Long> timeSlotIds, final ChannelJpaEntity channelEntity) {
        channelVoteRepository.deleteForVote(voteId, channelId);
        for (Long timeSlotId : timeSlotIds) {
            TimeSlotJpaEntity timeSlotEntity = timeSlotRepository.getTimeSlotById(timeSlotId);
            channelVoteRepository.save(ChannelVoteJpaEntity.create(timeSlotEntity, channelEntity));
        }
    }

    public void deleteVote(final Long recruitmentId, final Long voteId) {
        if (!voteRepository.existsVoteByRecruitment(recruitmentId)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }
        voteRepository.deleteVoteById(voteId);
    }
}
