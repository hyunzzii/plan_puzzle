package com.sloth.plan_puzzle.api.service.channel;

import com.sloth.plan_puzzle.api.service.subscription.SubscriptionService;
import com.sloth.plan_puzzle.api.service.user.UserService;
import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.domain.channel.Notice;
import com.sloth.plan_puzzle.dto.channel.ChannelRequest;
import com.sloth.plan_puzzle.dto.channel.NoticeRequest;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelFacade {
    private final UserService userService;
    private final NoticeService noticeService;
    private final ChannelService channelService;
    private final SubscriptionService subscriptionService;

    public Channel createChannel(final ChannelRequest channelRequest, final Long userId) {
        final UserJpaEntity userEntity = userService.getEntity(userId);
        final Channel channel = channelRequest.toDomain().validateImgUrl();
        return channelService.create(channel, userEntity);
    }

    public void deleteChannel(final Long channelId, final Long userId) {
        channelService.delete(channelId, userId);
    }

    public void updateChannel(final Long channelId, final ChannelRequest request, final Long userId) {
        final Channel channel = request.toDomain().validateImgUrl();
        channelService.update(channelId, channel, userId);
    }

    @Transactional(readOnly = true)
    public void isDuplicateNickname(final String nickname) {
        channelService.isDuplicateNickname(nickname);
    }

    @Transactional(readOnly = true)
    public Channel getChannel(final Long channelId) {
        return channelService.getChannel(channelId);
    }

    @Transactional(readOnly = true)
    public List<Channel> getChannelsByUserId(final Long userId) {
        return channelService.getChannelsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<Channel> getChannelsByPagingForSearch(final String keyword, final Pageable pageable) {
        return channelService.getChannelsByPagingForSearch(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Channel> getChannelsByPaging(final Pageable pageable) {
        return channelService.getChannelsByPaging(pageable);
    }

    //notice --------
    public Notice createNotice(final Long channelId, final NoticeRequest request, final Long userId) {
        channelService.verifyChannel(channelId, userId);
        final ChannelJpaEntity channelEntity = channelService.getEntity(channelId);
        final Notice notice = request.toDomain().validateImgUrl();
        return noticeService.create(notice, channelEntity);
    }

    public void deleteNotice(final Long channelId, final Long noticeId, final Long userId) {
        channelService.verifyChannel(channelId, userId);
        noticeService.delete(noticeId);
    }

    @Transactional(readOnly = true)
    public Page<Notice> getNoticesByPaging(final Long channelId, final Pageable pageable) {
        return noticeService.getNoticesByPaging(channelId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Notice> getRecentNoticesForSubscription(final Long channelId, final Long userId,
                                                        final LocalDateTime recent) {
        channelService.verifyChannel(channelId, userId);
        return noticeService.getRecentNoticesForSubscription(channelId, recent);
    }

    //subscription ----
    public void subscribe(final Long subscriberId, final Long subscribedId, final Long userId) {
        channelService.verifyChannel(subscribedId, userId);
        final ChannelJpaEntity subscriber = channelService.getEntity(subscriberId);
        final ChannelJpaEntity subscribed = channelService.getEntity(subscribedId);
        subscriptionService.subscribe(subscriber, subscribed);
    }

    public void unSubscribe(final Long subscriberId, final Long subscribedId, final Long userId) {
        channelService.verifyChannel(subscriberId, userId);
        subscriptionService.unSubscribe(subscriberId, subscribedId);
    }

    @Transactional(readOnly = true)
    public List<Channel> getSubscribedChannels(final Long channelId, final Long userId) {
        channelService.verifyChannel(channelId, userId);
        return subscriptionService.getSubscribedChannels(channelId);
    }
}
