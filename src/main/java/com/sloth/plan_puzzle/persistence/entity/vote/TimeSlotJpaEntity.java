package com.sloth.plan_puzzle.persistence.entity.vote;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "time_slot")
public class TimeSlotJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_slot_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private VoteJpaEntity vote;

    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<ChannelVoteJpaEntity> channelVotes = new ArrayList<>();

    @Builder
    private TimeSlotJpaEntity(final LocalDateTime startDateTime, final LocalDateTime endDateTime,
                              final VoteJpaEntity voteEntity) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.vote = voteEntity;
    }

    public static TimeSlotJpaEntity create(final LocalDateTime startDateTime, final LocalDateTime endDateTime,
                                           final VoteJpaEntity voteEntity) {
        return TimeSlotJpaEntity.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .voteEntity(voteEntity)
                .build();
    }
}
