package com.sloth.plan_puzzle.domain.channel;

import com.sloth.plan_puzzle.common.UrlValidator;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import lombok.Builder;

@Builder
public record Channel(
        Long id,
        String nickname,
        String introduction,
        String backImgUrl,
        String profileImgUrl
) {

    public static Channel fromEntity(final ChannelJpaEntity channelEntity) {
        return Channel.builder()
                .id(channelEntity.getId())
                .nickname(channelEntity.getNickname())
                .introduction(channelEntity.getIntroduction())
                .backImgUrl(channelEntity.getBackImgUrl())
                .profileImgUrl(channelEntity.getProfileImgUrl())
                .build();
    }

    public ChannelJpaEntity toEntity(final UserJpaEntity userEntity) {
        return ChannelJpaEntity.create(nickname, introduction, backImgUrl, profileImgUrl, userEntity);
    }

    public Channel validateImgUrl() {
        UrlValidator.isValidateUrl(backImgUrl);
        UrlValidator.isValidateUrl(profileImgUrl);
        return this;
    }
}
