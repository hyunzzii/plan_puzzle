package com.sloth.plan_puzzle.persistence.entity.recruitment;

import com.sloth.plan_puzzle.domain.recruitment.RecruitState;
import com.sloth.plan_puzzle.persistence.entity.BaseTimeEntity;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.participation.ParticipationJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.VoteJpaEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recruitment")
public class RecruitmentJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer recruitCapacity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitState recruitState;

    @Column(nullable = false)
    private String imgUrl;

    @Embedded
    @Column(nullable = false)
    private Region region;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "vote_id", nullable = true)
    private VoteJpaEntity vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = true)
    private ChannelJpaEntity author;

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<RecruitmentScheduleJpaEntity> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<ParticipationJpaEntity> participationList = new ArrayList<>();

    @Builder
    private RecruitmentJpaEntity(final String title, final String content, final Integer recruitCapacity,
                                 final RecruitState recruitState, final String imgUrl, final Region region,
                                 final ChannelJpaEntity author) {
        this.title = title;
        this.content = content;
        this.recruitCapacity = recruitCapacity;
        this.recruitState = recruitState;
        this.imgUrl = imgUrl;
        this.region = region;
        this.author = author;
    }

    public static RecruitmentJpaEntity create(final String title, final String content, final Integer recruitCapacity,
                                              final RecruitState recruitState, final String imgUrl, final Region region,
                                              final ChannelJpaEntity author) {
        return RecruitmentJpaEntity.builder()
                .title(title)
                .content(content)
                .recruitCapacity(recruitCapacity)
                .recruitState(recruitState)
                .imgUrl(imgUrl)
                .region(region)
                .author(author)
                .build();
    }

    public RecruitmentJpaEntity update(final String title, final String content, final Integer recruitCapacity,
                                       final String imgUrl, final Region region) {
        this.title = title;
        this.content = content;
        this.recruitCapacity = recruitCapacity;
        this.imgUrl = imgUrl;
        this.region = region;
        return this;
    }

    public RecruitmentJpaEntity patchState(final RecruitState recruitState) {
        this.recruitState = recruitState;
        return this;
    }

//    public RecruitmentJpaEntity patchTimeState(final TimeState timeState) {
//        this.timeState = timeState;
//        return this;
//    }
}
