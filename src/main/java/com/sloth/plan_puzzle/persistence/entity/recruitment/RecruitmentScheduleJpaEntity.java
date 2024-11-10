package com.sloth.plan_puzzle.persistence.entity.recruitment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recruitment-schedule")
public class RecruitmentScheduleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shcedule_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    private RecruitmentJpaEntity recruitment;

    @Builder
    private RecruitmentScheduleJpaEntity(final LocalDateTime startDateTime, final LocalDateTime endDateTime,
                                         final String title, final String content,
                                         final RecruitmentJpaEntity recruitmentEntity) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.title = title;
        this.content = content;
        this.recruitment = recruitmentEntity;
    }

    public static RecruitmentScheduleJpaEntity create(final LocalDateTime startDateTime,
                                                      final LocalDateTime endDateTime,
                                                      final String title, final String content,
                                                      final RecruitmentJpaEntity recruitmentEntity) {
        return RecruitmentScheduleJpaEntity.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .title(title)
                .content(content)
                .recruitmentEntity(recruitmentEntity)
                .build();
    }
}
