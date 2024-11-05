package com.sloth.plan_puzzle.dto.channel;


import com.sloth.plan_puzzle.domain.channel.Channel;
import lombok.Builder;

@Builder
public record ChannelResponse(
        Long id,

        String nickname,

        String introduction,

        String backImgUrl,

        String profileImgUrl
) {

    public static ChannelResponse fromDomain(final Channel channel){
        return ChannelResponse.builder()
                .id(channel.id())
                .nickname(channel.nickname())
                .introduction(channel.introduction())
                .backImgUrl(channel.backImgUrl())
                .profileImgUrl(channel.profileImgUrl())
                .build();
    }
}
