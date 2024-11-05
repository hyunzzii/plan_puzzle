package com.sloth.plan_puzzle.persistence.entity.channel;

import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.persistence.entity.BaseTimeEntity;
import com.sloth.plan_puzzle.persistence.entity.participation.ParticipationJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.subscription.SubscriptionJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "channel")
public class ChannelJpaEntity extends BaseTimeEntity {
    private final static String DEFAULT_PROFILE_IMG_URL = "/default";
    private final static String DEFAULT_BACK_IMG_URL = "/default";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String introduction;

    @Column(nullable = false)
    private String backImgUrl;

    @Column(nullable = false)
    private String profileImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserJpaEntity user;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<SubscriptionJpaEntity> subscriberList = new ArrayList<>();

    @OneToMany(mappedBy = "subscribed", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<SubscriptionJpaEntity> subscribedList = new ArrayList<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<ParticipationJpaEntity> participationList = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private final List<RecruitmentJpaEntity> recruitments = new ArrayList<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<NoticeJpaEntity> notices = new ArrayList<>();

    @Builder
    public ChannelJpaEntity(final String nickname, final String introduction, final String backImgUrl,
                            final String profileImgUrl, final UserJpaEntity user) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.backImgUrl = backImgUrl == null ? DEFAULT_BACK_IMG_URL : backImgUrl;
        this.profileImgUrl = profileImgUrl == null ? DEFAULT_PROFILE_IMG_URL : backImgUrl;
        this.user = user;
    }

    public void update(final Channel channel) {
        nickname = channel.nickname();
        introduction = channel.introduction();
        backImgUrl = channel.backImgUrl();
        profileImgUrl = channel.profileImgUrl();
    }
}
