package com.sloth.plan_puzzle.api.controller.recruitment;

import com.sloth.plan_puzzle.api.service.recruitment.RecruitmentFacade;
import com.sloth.plan_puzzle.common.security.jwt.CustomUserDetails;
import com.sloth.plan_puzzle.domain.vote.Vote;
import com.sloth.plan_puzzle.dto.channel.SimpleChannelResponse;
import com.sloth.plan_puzzle.dto.recruitment.response.RecruitmentResponse;
import com.sloth.plan_puzzle.dto.vote.response.VoteIdResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitments")
public class RecruitmentController {
    private static final int PAGE_SIZE = 20;

    private final RecruitmentFacade recruitmentFacade;

    @GetMapping("/{channelId}")
    public Page<RecruitmentResponse> getRecruitmentsOfChannel(@PathVariable Long channelId,
                                                              @RequestParam("page") Integer page) {
        final Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdDate").descending());
        return recruitmentFacade.getChannelRecruitments(channelId, pageable);
    }

    @GetMapping("/{recruitId}")
    public RecruitmentResponse getRecruitment(@PathVariable Long recruitId) {
        return recruitmentFacade.getRecruitment(recruitId);
    }

    @GetMapping
    public Page<RecruitmentResponse> getRecruitments(@RequestParam(value = "keyword", required = false) String keyword,
                                                     @RequestParam("page") Integer page) {
        final Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdDate").descending());
        return recruitmentFacade.getRecruitmentListForSearch(keyword, pageable);
    }

    @GetMapping("/{recruitmentId}/votes")
    public VoteIdResponse getVote(@PathVariable Long recruitmentId) {
        final Vote vote = recruitmentFacade.getVote(recruitmentId);
        return VoteIdResponse.fromDomain(vote);
    }

    @GetMapping("/{recruitmentId}/{scheduleId}/availability")
    public boolean isAvailableRecruitSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long recruitmentId,
                                              @PathVariable Long scheduleId) {
        final Long userId = userDetails.getUserId();
        return recruitmentFacade.isAvailableSchedule(scheduleId, userId);
    }

    @GetMapping("/{recruitmentId}/participation")
    public List<SimpleChannelResponse> getParticipationChannels(@PathVariable("recruitmentId") Long recruitmentId) {
        return SimpleChannelResponse.fromDomainList(
                recruitmentFacade.getParticipationChannels(recruitmentId));
    }
}
