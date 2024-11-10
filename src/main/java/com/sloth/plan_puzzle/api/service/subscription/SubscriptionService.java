package com.sloth.plan_puzzle.api.service.subscription;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.SUBSCRIPTION_NOT_EXIST;

import com.sloth.plan_puzzle.common.exception.CustomException;
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
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public void subscribe(final ChannelJpaEntity subscriber, final ChannelJpaEntity subscribed) {
        if(subscriptionRepository.existsBySubscriberIdAndSubscribedId(subscriber.getId(),subscribed.getId())){
            throw new CustomException(SUBSCRIPTION_NOT_EXIST);
        }
        subscriptionRepository.save(SubscriptionJpaEntity.create(subscriber, subscribed));
    }

    public void unSubscribe(final Long subscriberId, final Long subscribedId) {
        subscriptionRepository.deleteSubscriptionBySubscribe(subscriberId, subscribedId);
    }

    public List<Channel> getSubscribedChannels(final Long channelId) {
        List<SubscriptionJpaEntity> subscriptionEntityList = subscriptionRepository.findBySubscriberId(channelId);
        return subscriptionEntityList.stream()
                .map(subscription -> Channel.fromEntity(subscription.getSubscribed()))
                .toList();
    }
}
