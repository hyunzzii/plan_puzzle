package com.sloth.plan_puzzle.api.controller.recruitment;


import com.sloth.plan_puzzle.api.service.recruitment.RecruitmentFacade;
import com.sloth.plan_puzzle.common.security.jwt.CustomUserDetails;
import com.sloth.plan_puzzle.dto.ListWrapperResponse;
import com.sloth.plan_puzzle.dto.recruitment.CreateRecruitmentRequest;
import com.sloth.plan_puzzle.dto.recruitment.CreateRecruitmentScheduleRequest;
import com.sloth.plan_puzzle.dto.recruitment.PatchRecruitmentStateRequest;
import com.sloth.plan_puzzle.dto.recruitment.response.RecruitmentIdResponse;
import com.sloth.plan_puzzle.dto.vote.CreateVoteRequest;
import com.sloth.plan_puzzle.dto.vote.VoteRequest;
import com.sloth.plan_puzzle.dto.vote.response.SimpleTimeSlotResponse;
import com.sloth.plan_puzzle.dto.vote.response.TimeSlotResponse;
import com.sloth.plan_puzzle.dto.vote.response.VoteIdResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels/{channelId}/recruitments")
public class AuthorizedRecruitmentController {
    private final RecruitmentFacade recruitmentFacade;

    @PostMapping
    public RecruitmentIdResponse createRecruitment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable Long channelId,
                                                   @RequestBody @Valid CreateRecruitmentRequest request) {
        final Long userId = userDetails.getUserId();
        return RecruitmentIdResponse.fromDomain(
                recruitmentFacade.createRecruitment(channelId, request, userId));
    }

    @PutMapping("/{recruitmentId}")
    public void updateRecruitment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long channelId,
                                  @PathVariable Long recruitmentId,
                                  @RequestBody @Valid CreateRecruitmentRequest request) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.updateRecruitment(channelId, recruitmentId, request, userId);
    }

    @DeleteMapping("/{recruitmentId}")
    public void deleteRecruitment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long channelId,
                                  @PathVariable Long recruitmentId) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.deleteRecruitment(channelId, recruitmentId, userId);
    }

    @PatchMapping("/{recruitmentId}")
    public void patchRecruitmentState(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @PathVariable Long channelId,
                                      @PathVariable Long recruitmentId,
                                      @RequestBody @Valid PatchRecruitmentStateRequest request) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.patchRecruitmentState(channelId, recruitmentId, request, userId);
    }


    @PostMapping("/{recruitmentId}/participants")
    public void participate(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @PathVariable Long channelId,
                            @PathVariable Long recruitmentId) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.participate(channelId, recruitmentId, userId);
    }

    @DeleteMapping("/{recruitmentId}/participants")
    public void withdraw(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @PathVariable Long channelId,
                         @PathVariable Long recruitmentId) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.withdraw(channelId, recruitmentId, userId);
    }

    @GetMapping("/{recruitmentId}/availability")
    public ListWrapperResponse<SimpleTimeSlotResponse> getAvailableTimesForVote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId,
            @RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end,
            @RequestParam("duration") Integer duration) {
        final Long userId = userDetails.getUserId();
        final List<SimpleTimeSlotResponse> timeSlotResponses = SimpleTimeSlotResponse.fromDomainList(
                recruitmentFacade.getAvailableTimesForVote(channelId, recruitmentId, start, end, duration, userId));
        return ListWrapperResponse.of(timeSlotResponses);
    }

    @GetMapping("/{recruitmentId}/votes")
    public VoteIdResponse getVote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId) {
        final Long userId = userDetails.getUserId();
        return VoteIdResponse.fromDomain(
                recruitmentFacade.getVote(recruitmentId));
    }

    @GetMapping("/{recruitmentId}/votes/{voteId}")
    public ListWrapperResponse<TimeSlotResponse> getTimeslotsOfVote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId,
            @PathVariable Long voteId) {
        final Long userId = userDetails.getUserId();
        return ListWrapperResponse.of(
                recruitmentFacade.getAllTimeSlotsOfVote(channelId, recruitmentId, voteId, userId));
    }

    @PostMapping("/{recruitmentId}/votes")
    public VoteIdResponse createVote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId,
            @RequestBody @Valid CreateVoteRequest request) {
        final Long userId = userDetails.getUserId();
        return VoteIdResponse.fromDomain(
                recruitmentFacade.createVote(channelId, recruitmentId, request, userId));
    }

    @DeleteMapping("/{recruitmentId}/votes/{voteId}")
    public void deleteVote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId,
            @PathVariable Long voteId) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.deleteVote(channelId, recruitmentId, voteId, userId);
    }

    @PostMapping("/{recruitmentId}/votes/{voteId}")
    public void vote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId,
            @PathVariable Long voteId,
            @RequestBody @Valid VoteRequest request) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.vote(channelId, recruitmentId, voteId, request, userId);
    }

    @PostMapping("/{recruitmentId}/schedules/send")
    public void addSchedulesToParticipation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.addSchedulesToParticipation(channelId, recruitmentId, userId);
    }

    @PostMapping("/{recruitmentId}/schedules/send/user")
    public void addSchedulesToUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.addSchedulesToUser(channelId, recruitmentId, userId);
    }

    @PostMapping("/{recruitmentId}/schedules/send/{scheduleId}")
    public void addScheduleToParticipation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId,
            @PathVariable Long scheduleId) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.addScheduleToParticipation(channelId, recruitmentId, scheduleId, userId);
    }

    @PostMapping("/{recruitmentId}/schedules")
    public void createSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId,
            @RequestBody @Valid CreateRecruitmentScheduleRequest request) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.createSchedule(channelId, recruitmentId, request, userId);
    }

    @DeleteMapping("/{recruitmentId}/schedules/{scheduleId}")
    public void deleteSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long channelId,
            @PathVariable Long recruitmentId,
            @PathVariable Long scheduleId) {
        final Long userId = userDetails.getUserId();
        recruitmentFacade.deleteSchedule(channelId, recruitmentId, scheduleId, userId);
    }
}