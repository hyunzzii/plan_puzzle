package com.sloth.plan_puzzle.persistence.entity.channel;

import com.sloth.plan_puzzle.persistence.entity.BaseTimeEntity;
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
@Table(name = "notice")
public class NoticeJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_Id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private ChannelJpaEntity channel;

    @Builder
    private NoticeJpaEntity(final String title, final String content, final String imgUrl,
                           final ChannelJpaEntity channelEntity) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.channel = channelEntity;
    }

    public static NoticeJpaEntity create(final String title, final String content, final String imgUrl,
                                         final ChannelJpaEntity channelEntity){
        return NoticeJpaEntity.builder()
                .title(title)
                .content(content)
                .imgUrl(imgUrl)
                .channelEntity(channelEntity)
                .build();
    }

    public static NoticeJpaEntity create(final String title, final String content,
                                         final ChannelJpaEntity channelEntity){
        return NoticeJpaEntity.builder()
                .title(title)
                .content(content)
                .channelEntity(channelEntity)
                .build();
    }
}
