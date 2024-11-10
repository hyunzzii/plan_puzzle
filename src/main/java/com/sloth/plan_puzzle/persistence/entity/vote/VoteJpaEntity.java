package com.sloth.plan_puzzle.persistence.entity.vote;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.VOTE_ALREADY_EXISTS;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "vote")
public class VoteJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    private RecruitmentJpaEntity recruitment;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<TimeSlotJpaEntity> timeSlotList = new ArrayList<>();

    private LocalDateTime deadline;

    @Builder
    private VoteJpaEntity(final LocalDateTime deadline, final RecruitmentJpaEntity recruitmentEntity) {
        this.deadline = deadline;
        this.recruitment = recruitmentEntity;
    }

    public static VoteJpaEntity create(final LocalDateTime deadline, final RecruitmentJpaEntity recruitmentEntity) {
        if (recruitmentEntity.getVote() != null) {
            throw new CustomException(VOTE_ALREADY_EXISTS);
        }
        return VoteJpaEntity.builder()
                .deadline(deadline)
                .recruitmentEntity(recruitmentEntity)
                .build();
    }
}
