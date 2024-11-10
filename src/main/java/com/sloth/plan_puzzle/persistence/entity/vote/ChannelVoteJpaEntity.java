package com.sloth.plan_puzzle.persistence.entity.vote;

import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import jakarta.persistence.CascadeType;
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
@Table(name = "channel_vote")
public class ChannelVoteJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    private TimeSlotJpaEntity timeSlot;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "channel_id")
    private ChannelJpaEntity channel;

    @Builder
    private ChannelVoteJpaEntity(final TimeSlotJpaEntity timeSlotEntity, final ChannelJpaEntity channelEntity) {
        this.timeSlot = timeSlotEntity;
        this.channel = channelEntity;
    }

    public static ChannelVoteJpaEntity create(final TimeSlotJpaEntity timeSlotEntity,
                                              final ChannelJpaEntity channelEntity) {
        return ChannelVoteJpaEntity.builder()
                .timeSlotEntity(timeSlotEntity)
                .channelEntity(channelEntity)
                .build();
    }
}
