package com.sloth.plan_puzzle.persistence.entity.participation;

import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
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
@Table(name = "participation")
public class ParticipationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    private RecruitmentJpaEntity recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private ChannelJpaEntity channel;

    @Builder
    public ParticipationJpaEntity(final RecruitmentJpaEntity recruitmentEntity, final ChannelJpaEntity channelEntity) {
        this.recruitment = recruitmentEntity;
        this.channel = channelEntity;
    }

    public static ParticipationJpaEntity create(final RecruitmentJpaEntity recruitmentEntity,
                                                final ChannelJpaEntity channelEntity) {
        return ParticipationJpaEntity.builder()
                .recruitmentEntity(recruitmentEntity)
                .channelEntity(channelEntity)
                .build();
    }
}
