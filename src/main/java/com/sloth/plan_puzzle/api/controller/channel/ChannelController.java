package com.sloth.plan_puzzle.api.controller.channel;

import com.sloth.plan_puzzle.api.service.channel.ChannelFacade;
import com.sloth.plan_puzzle.common.security.jwt.CustomUserDetails;
import com.sloth.plan_puzzle.dto.ListWrapperResponse;
import com.sloth.plan_puzzle.dto.channel.ChannelRequest;
import com.sloth.plan_puzzle.dto.channel.ChannelResponse;
import com.sloth.plan_puzzle.dto.channel.NoticeRequest;
import com.sloth.plan_puzzle.dto.channel.NoticeResponse;
import com.sloth.plan_puzzle.dto.channel.SimpleChannelResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {
    private static final int PAGE_SIZE = 20;
    private final ChannelFacade channelFacade;

    @GetMapping("/validation/nickname")
    public void validateNickname(@RequestParam("nickname") String nickname) {
        channelFacade.isDuplicateNickname(nickname);
    }

    @PostMapping("/user")
    public void createChannel(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestBody @Valid ChannelRequest request) {
        final Long userId = userDetails.getUserId();
        channelFacade.createChannel(request, userId);
    }

    @GetMapping("/user")
    public ListWrapperResponse<SimpleChannelResponse> getMyChannels(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        final Long userId = userDetails.getUserId();
        return ListWrapperResponse.of(
                SimpleChannelResponse.fromDomainList(channelFacade.getChannelsByUserId(userId)));
    }

    @PutMapping("/user/{channelId}")
    public void modifyMyChannel(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @PathVariable Long channelId, @RequestBody @Valid ChannelRequest request) {
        final Long userId = userDetails.getUserId();
        channelFacade.updateChannel(channelId, request, userId);
    }

    @DeleteMapping("/user/{channelId}")
    public void deleteMyChannel(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @PathVariable Long channelId) {
        final Long userId = userDetails.getUserId();
        channelFacade.deleteChannel(channelId, userId);
    }

    @GetMapping
    public Page<SimpleChannelResponse> getChannels(@RequestParam("page") Integer page) {
        final Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdDate").descending());
        return channelFacade.getChannelsByPaging(pageable)
                .map(SimpleChannelResponse::fromDomain);
    }

    @GetMapping("/search")
    public Page<SimpleChannelResponse> getChannelsForSearch(@RequestParam("page") Integer page,
                                                            @RequestParam("keyword") String keyword) {
        final Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdDate").descending());
        return channelFacade.getChannelsByPagingForSearch(keyword, pageable)
                .map(SimpleChannelResponse::fromDomain);
    }

    @GetMapping("/{channelId}")
    public ChannelResponse getChannel(@PathVariable Long channelId) {
        return ChannelResponse.fromDomain(channelFacade.getChannel(channelId));
    }

    @PostMapping("/{channelId}/notices")
    public void createNotice(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long channelId, @RequestBody @Valid NoticeRequest request) {
        final Long userId = userDetails.getUserId();
        channelFacade.createNotice(channelId, request, userId);
    }

    @GetMapping("/{channelId}/notices")
    public Page<NoticeResponse> getNotices(@PathVariable Long channelId,
                                           @RequestParam Integer page) {
        final Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdDate").descending());
        return channelFacade.getNoticesByPaging(channelId, pageable)
                .map(NoticeResponse::fromEntity);
    }

    @DeleteMapping("/{channelId}/notices/{noticeId}")
    public void deleteNotice(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long channelId,
                             @PathVariable Long noticeId) {
        final Long userId = userDetails.getUserId();
        channelFacade.deleteNotice(channelId, noticeId, userId);
    }
}
