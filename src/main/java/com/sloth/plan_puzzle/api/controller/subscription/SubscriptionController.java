package com.sloth.plan_puzzle.api.controller.subscription;

import com.sloth.plan_puzzle.api.service.channel.NoticeService;
import com.sloth.plan_puzzle.api.service.subscription.SubscriptionService;
import com.sloth.plan_puzzle.common.security.jwt.CustomUserDetails;
import com.sloth.plan_puzzle.dto.ListWrapperResponse;
import com.sloth.plan_puzzle.dto.channel.NoticeResponse;
import com.sloth.plan_puzzle.dto.channel.SimpleChannelResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final NoticeService noticeService;

    @PostMapping("/{subscriberId}/{subscribedId}")
    public void subscribe(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @PathVariable Long subscriberId,
                          @PathVariable Long subscribedId) {
        final Long userId = userDetails.getUserId();
        subscriptionService.subscribe(subscriberId, subscribedId, userId);
    }

    @DeleteMapping("/{subscriberId}/{subscribedId}")
    public void unSubscribe(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @PathVariable Long subscriberId,
                            @PathVariable Long subscribedId) {
        final Long userId = userDetails.getUserId();
        subscriptionService.unSubscribe(subscriberId, subscribedId, userId);
    }

    @GetMapping("/{channelId}")
    public ListWrapperResponse<SimpleChannelResponse> getSubscribedChannel(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long channelId) {
        final Long userId = userDetails.getUserId();
        return ListWrapperResponse.of(SimpleChannelResponse.fromDomainList(
                subscriptionService.getSubscribedChannels(channelId, userId)));
    }

    @GetMapping("/{channelId}/notices/new")
    public ListWrapperResponse<NoticeResponse> getRecentNoticesForSubscription(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long channelId) {
        final Long userId = userDetails.getUserId();
        final LocalDateTime oneWeekAgo = LocalDate.now().minusWeeks(1).atStartOfDay();
        return ListWrapperResponse.of(NoticeResponse.fromEntityList(
                noticeService.getRecentNoticesForSubscription(channelId, userId, oneWeekAgo)
        ));
    }
}
