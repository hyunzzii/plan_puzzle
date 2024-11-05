package com.sloth.plan_puzzle.dto.channel;

import com.sloth.plan_puzzle.domain.channel.Channel;
import java.util.List;
import lombok.Builder;

@Builder
public record SimpleChannelResponse (
        Long id,

        String nickname,

        String profileImgUrl
){
    public static List<SimpleChannelResponse> fromDomainList(final List<Channel> channels) {
        return channels.stream()
                .map(SimpleChannelResponse::fromDomain)
                .toList();
    }
    public static SimpleChannelResponse fromDomain(final Channel channel){
        return SimpleChannelResponse.builder()
                .id(channel.id())
                .nickname(channel.nickname())
                .profileImgUrl(channel.profileImgUrl())
                .build();
    }
}
