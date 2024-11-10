package com.sloth.plan_puzzle.persistence.repository.subscription;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.SUBSCRIPTION_NOT_EXIST;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.subscription.SubscriptionJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository extends JpaRepository<SubscriptionJpaEntity, Long> {

    @Query("SELECT s FROM SubscriptionJpaEntity s WHERE s.subscriber.id = :subscriberId AND s.subscribed.id = :subscribedId")
    Optional<SubscriptionJpaEntity> findBySubscriberIdAndSubscribedId(Long subscriberId, Long subscribedId);

    @Query("SELECT COUNT(s)>0 FROM SubscriptionJpaEntity s WHERE s.subscriber.id = :subscriberId AND s.subscribed.id = :subscribedId")
    boolean existsBySubscriberIdAndSubscribedId(Long subscriberId, Long subscribedId);

    @Query("SELECT s FROM SubscriptionJpaEntity s WHERE s.subscriber.id = :subscriberId")
    List<SubscriptionJpaEntity> findBySubscriberId(Long subscriberId);

    default void deleteSubscriptionBySubscribe(final Long subscriberId, final Long subscribedId) {
        SubscriptionJpaEntity subscriptionEntity = findBySubscriberIdAndSubscribedId(subscriberId, subscribedId)
                .orElseThrow(() -> new CustomException(SUBSCRIPTION_NOT_EXIST));
        delete(subscriptionEntity);
    }
}
