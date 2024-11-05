package com.sloth.plan_puzzle.api.service.subscription;

import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.subscription.SubscriptionJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.subscription.SubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService {
    private SubscriptionRepository subscriptionRepository;
    private ChannelRepository channelRepository;

    public void subscribe(final Long subscriberId, final Long subscribedId, final Long userId) {
        final ChannelJpaEntity subscriber = channelRepository.getChannelByIdAndUserId(subscriberId, userId);
        final ChannelJpaEntity subscribed = channelRepository.getChannelById(subscribedId);
        subscriptionRepository.save(SubscriptionJpaEntity.create(subscriber, subscribed));
    }

    public void unSubscribe(final Long subscriberId, final Long subscribedId, final Long userId) {
        channelRepository.existsByIdAndUserId(subscriberId, userId);
        subscriptionRepository.deleteSubscriptionBySubscribe(subscriberId, subscribedId);
    }

    @Transactional(readOnly = true)
    public List<Channel> getSubscribedChannels(final Long channelId, final Long userId) {
        channelRepository.existsByIdAndUserId(channelId, userId);
        List<SubscriptionJpaEntity> subscriptionEntityList = subscriptionRepository.findBySubscriberId(channelId);
        return subscriptionEntityList.stream()
                .map(subscription -> Channel.fromEntity(subscription.getSubscribed()))
                .toList();
    }
}
