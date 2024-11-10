package com.sloth.plan_puzzle.api.service.recruitment;


import static com.sloth.plan_puzzle.common.validator.TimeValidator.TimeFormatUnit;

import com.sloth.plan_puzzle.api.service.channel.ChannelService;
import com.sloth.plan_puzzle.api.service.participation.ParticipationService;
import com.sloth.plan_puzzle.api.service.schedule.UserScheduleService;
import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.domain.recruitment.Recruitment;
import com.sloth.plan_puzzle.domain.recruitment.RecruitmentSchedule;
import com.sloth.plan_puzzle.domain.vote.TimeSlot;
import com.sloth.plan_puzzle.domain.vote.Vote;
import com.sloth.plan_puzzle.dto.recruitment.CreateRecruitmentRequest;
import com.sloth.plan_puzzle.dto.recruitment.CreateRecruitmentScheduleRequest;
import com.sloth.plan_puzzle.dto.recruitment.PatchRecruitmentStateRequest;
import com.sloth.plan_puzzle.dto.recruitment.response.RecruitmentResponse;
import com.sloth.plan_puzzle.dto.vote.CreateVoteRequest;
import com.sloth.plan_puzzle.dto.vote.VoteRequest;
import com.sloth.plan_puzzle.dto.vote.response.TimeSlotResponse;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitmentFacade {
    private final ChannelService channelService;
    private final RecruitmentService recruitmentService;
    private final RecruitmentVoteService recruitmentVoteService;
    private final ParticipationService participationService;
    private final RecruitmentScheduleService recruitmentScheduleService;
    private final UserScheduleService userScheduleService;

    public Recruitment createRecruitment(final Long channelId, final CreateRecruitmentRequest request,
                                         final Long userId) {
        channelService.verifyChannel(channelId, userId);
        final Recruitment recruitment = request.toDomain().validateImgUrl();
        final ChannelJpaEntity channelEntity = channelService.getEntity(channelId);
        return recruitmentService.create(recruitment, channelEntity);
    }

    public void updateRecruitment(final Long channelId, final Long recruitmentId,
                                  final CreateRecruitmentRequest request, final Long userId) {
        System.out.println("updateRecruitment In");
        verifyAuthorByUser(recruitmentId, channelId, userId);
        final Recruitment recruitment = request.toDomain().validateImgUrl();
        recruitmentService.update(recruitment, recruitmentId);
        System.out.println("updateRecruitment Out");
    }

    public void deleteRecruitment(final Long channelId, final Long recruitmentId, final Long userId) {
        verifyAuthorByUser(recruitmentId, channelId, userId);
        recruitmentService.deleteById(recruitmentId);
    }

    public void patchRecruitmentState(final Long channelId, final Long recruitmentId,
                                      final PatchRecruitmentStateRequest request, final Long userId) {
        verifyAuthorByUser(recruitmentId, channelId, userId);
        recruitmentService.patchState(recruitmentId, request.recruitState());
    }

    @Transactional(readOnly = true)
    public RecruitmentResponse getRecruitment(final Long recruitmentId) {
        return recruitmentService.getRecruitment(recruitmentId);
    }

    @Transactional(readOnly = true)
    public Page<RecruitmentResponse> getChannelRecruitments(final Long channelId, final Pageable pageable) {
        return recruitmentService.getChannelRecruitments(channelId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<RecruitmentResponse> getRecruitmentListForSearch(final String keyword, final Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return recruitmentService.getRecruitmentList(pageable);
        }

        return recruitmentService.getRecruitmentListForSearch(keyword, pageable);
    }

    // participation---------
    public void participate(final Long channelId, final Long recruitId, final Long userId) {
        channelService.verifyChannel(channelId, userId);
        final RecruitmentJpaEntity recruitmentEntity = recruitmentService.getEntity(recruitId);
        final ChannelJpaEntity channelEntity = channelService.getEntity(channelId);
        participationService.participate(recruitmentEntity, channelEntity);
    }

    public void withdraw(final Long channelId, final Long recruitmentId, final Long userId) {
        channelService.verifyChannel(channelId, userId);
        participationService.withdraw(recruitmentId, channelId);
    }

    @Transactional(readOnly = true)
    public List<Channel> getParticipationChannels(final Long recruitmentId) {
        return participationService.getChannelsByParticipation(recruitmentId);
    }


    //vote service---------
    public Vote createVote(final Long channelId, final Long recruitmentId,
                           final CreateVoteRequest request, final Long userId) {
        final RecruitmentJpaEntity recruitmentEntity = recruitmentService.getEntity(recruitmentId);
        final Vote vote = request.toVote().validate();
        final List<TimeSlot> timeSlots = request.toTimeSlotList().stream()
                .map(TimeSlot::validate)
                .toList();
        verifyAuthorByUser(recruitmentId, channelId, userId);
        return recruitmentVoteService.createVote(recruitmentId, vote, timeSlots, recruitmentEntity);
    }

    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getAllTimeSlotsOfVote(final Long channelId, final Long recruitmentId,
                                                        final Long voteId, final Long userId) {
        channelService.verifyChannel(channelId, userId);
        return recruitmentVoteService.getTimeSlotsAndVoters(voteId);
    }

    @Transactional(readOnly = true)
    public Vote getVote(final Long recruitmentId) {
        return recruitmentVoteService.getVote(recruitmentId);
    }


    public void vote(final Long channelId, final Long recruitmentId,
                     final Long voteId, final VoteRequest request, final Long userId) {
        channelService.verifyChannel(channelId, userId);
        participationService.verifyParticipation(recruitmentId, channelId);

        final ChannelJpaEntity channelEntity = channelService.getEntity(channelId);
        recruitmentVoteService.vote(channelId, voteId, request.timeSlotIds(), channelEntity);
    }

    public void deleteVote(final Long channelId, final Long recruitmentId,
                           final Long voteId, final Long userId) {
        verifyAuthorByUser(recruitmentId, channelId, userId);
        recruitmentVoteService.deleteVote(recruitmentId, voteId);
    }

    public List<TimeSlot> getAvailableTimesForVote(final Long channelId, final Long recruitmentId,
                                                   final LocalDate startDate, final LocalDate endDate,
                                                   final Integer duration, final Long userId) {
        verifyAuthorByUser(recruitmentId, channelId, userId);
        final RecruitmentJpaEntity recruitmentEntity = recruitmentService.getEntity(recruitmentId);
        final List<Long> participationIds = recruitmentEntity.getParticipationList().stream()
                .map(participation -> participation.getChannel().getUser().getId())
                .toList();
        return getCommonAvailableTimeSlots(startDate.atStartOfDay(), endDate.atStartOfDay(), duration,
                participationIds);
    }


    private List<TimeSlot> getCommonAvailableTimeSlots(final LocalDateTime startDateTime,
                                                       final LocalDateTime endDateTime,
                                                       final Integer duration, final List<Long> participationIds) {
        List<TimeSlot> availableTimeSlots = new ArrayList<>();
        LocalDateTime currentTime = startDateTime;

        List<TimeSlot> schedules = getParticipationSchedules(startDateTime, endDateTime, participationIds);

        int index = 0;
        while (!currentTime.plusMinutes(duration).isAfter(endDateTime)) {
            boolean isAvailable = true;
            LocalDateTime slotEndTime = currentTime.plusMinutes(duration);
            for (int i = index; i < schedules.size(); i++) {
                TimeSlot schedule = schedules.get(i);

                if (!(currentTime.isAfter(schedule.endDateTime())
                        || !slotEndTime.isAfter(schedule.startDateTime()))) {
                    currentTime = schedule.endDateTime();
                    isAvailable = false;
                    index++;
                    break;
                }
            }
            if (isAvailable) {
                availableTimeSlots.add(TimeSlot.create(currentTime, slotEndTime));
                currentTime = currentTime.plusMinutes(TimeFormatUnit);
            }
        }
        return availableTimeSlots;
    }

    private List<TimeSlot> getParticipationSchedules(final LocalDateTime startDateTime,
                                                     final LocalDateTime endDateTime,
                                                     final List<Long> participationIds) {
        List<TimeSlot> userSchedules = new ArrayList<>();
        for (Long userId : participationIds) {
            userScheduleService.getWithinPeriod(startDateTime, endDateTime, userId)
                    .forEach(s -> userSchedules.add(TimeSlot.create(s.startDateTime(), s.endDateTime())));
        }
        userSchedules.sort(Comparator.comparing(TimeSlot::startDateTime));
        return userSchedules;
    }

    //recruitment-schedule ---------
    public boolean isAvailableSchedule(final Long scheduleId, final Long userId) {
        final RecruitmentSchedule recruitmentSchedule = recruitmentScheduleService.getSchedule(scheduleId);
        final TimeSlot timeSlot = TimeSlot.create(recruitmentSchedule.startDateTime(),
                recruitmentSchedule.endDateTime());
        return userScheduleService.isAvailableSchedule(timeSlot, userId);
    }

    public void createSchedule(final Long channelId, final Long recruitmentId,
                               final CreateRecruitmentScheduleRequest request, final Long userId) {
        verifyAuthorByUser(recruitmentId, channelId, userId);
        final RecruitmentSchedule recruitmentSchedule = request.toDomain().validateSchedule();
        final RecruitmentJpaEntity recruitmentEntity = recruitmentService.getEntity(recruitmentId);
        recruitmentScheduleService.create(recruitmentSchedule, recruitmentEntity);
    }

    public void deleteSchedule(final Long channelId, final Long recruitmentId,
                               final Long scheduleId, final Long userId) {
        verifyAuthorByUser(recruitmentId, channelId, userId);
        recruitmentScheduleService.delete(scheduleId);
    }

    public void addSchedulesToParticipation(final Long channelId, final Long recruitmentId, final Long userId) {
        verifyAuthorByUser(recruitmentId, channelId, userId);

        final List<UserJpaEntity> participationUsers = recruitmentService.getEntity(recruitmentId)
                .getParticipationList()
                .stream()
                .map(participationEntity -> participationEntity.getChannel().getUser())
                .toList();

        participationUsers.forEach(participation ->
                putScheduleToUser(recruitmentScheduleService.getRecruitmentSchedules(recruitmentId),
                        participation));
    }


    public void addSchedulesToUser(final Long channelId, final Long recruitmentId, final Long userId) {
        List<RecruitmentSchedule> recruitmentSchedules = recruitmentScheduleService.getRecruitmentSchedules(
                recruitmentId);
        final UserJpaEntity userEntity = participationService.getChannelEntity(recruitmentId, channelId).getUser();
        putScheduleToUser(recruitmentSchedules, userEntity);
    }

    private void putScheduleToUser(final List<RecruitmentSchedule> recruitmentSchedules,
                                   final UserJpaEntity userEntity) {
        recruitmentSchedules.forEach(recruitmentSchedule -> {
            userScheduleService.create(recruitmentSchedule.toUserSchedule(), userEntity);
        });
    }

    public void addScheduleToParticipation(final Long channelId, final Long recruitmentId, final Long scheduleId,
                                           final Long userId) {
        verifyAuthorByUser(recruitmentId, channelId, userId);

        final List<UserJpaEntity> participationUsers = recruitmentService.getEntity(recruitmentId)
                .getParticipationList()
                .stream()
                .map(participationEntity -> participationEntity.getChannel().getUser())
                .toList();

        participationUsers.forEach(participation ->
                putScheduleToUser(recruitmentScheduleService.getSchedule(scheduleId), participation));
    }

    private void putScheduleToUser(final RecruitmentSchedule recruitmentSchedule,
                                   final UserJpaEntity userEntity) {
        userScheduleService.create(recruitmentSchedule.toUserSchedule(), userEntity);
    }

    //for Service---------
    @Transactional(readOnly = true)
    public void verifyAuthorByUser(final Long recruitmentId, final Long channelId, final Long userId) {
        channelService.verifyChannel(channelId, userId);
        recruitmentService.verifyAuthor(recruitmentId, channelId);
    }
}