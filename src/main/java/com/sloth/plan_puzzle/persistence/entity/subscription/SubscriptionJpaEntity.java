package com.sloth.plan_puzzle.persistence.entity.subscription;

import com.sloth.plan_puzzle.persistence.entity.BaseTimeEntity;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "subscription")
public class SubscriptionJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id")
    private ChannelJpaEntity subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscribed_id")
    private ChannelJpaEntity subscribed;

    @Builder
    private SubscriptionJpaEntity(final ChannelJpaEntity subscriberEntity, final ChannelJpaEntity subscribedEntity) {
        this.subscriber = subscriberEntity;
        this.subscribed = subscribedEntity;
    }

    public static SubscriptionJpaEntity create(final ChannelJpaEntity subscriberEntity,
                                               final ChannelJpaEntity subscribedEntity) {
        return SubscriptionJpaEntity.builder()
                .subscriberEntity(subscriberEntity)
                .subscribedEntity(subscribedEntity)
                .build();
    }
}
