package com.sloth.plan_puzzle.domain.channel;

import com.sloth.plan_puzzle.common.validator.UrlValidator;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.channel.NoticeJpaEntity;
import lombok.Builder;

@Builder
public record Notice(
        Long id,

        String title,

        String content,

        String imgUrl
) {
    public static Notice fromEntity(final NoticeJpaEntity noticeEntity) {
        return Notice.builder()
                .id(noticeEntity.getId())
                .title(noticeEntity.getTitle())
                .content(noticeEntity.getContent())
                .imgUrl(noticeEntity.getImgUrl())
                .build();
    }

    public NoticeJpaEntity toEntity(final ChannelJpaEntity channelEntity) {
        return NoticeJpaEntity.create(title, content, imgUrl, channelEntity);
    }

    public Notice validateImgUrl() {
        UrlValidator.isValidateUrl(imgUrl);
        return this;
    }
}
