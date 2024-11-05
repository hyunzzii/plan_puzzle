package com.sloth.plan_puzzle.dto.channel;

import com.sloth.plan_puzzle.domain.channel.Channel;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChannelRequest(
        @NotBlank
        String nickname,

        String introduction,

        @NotBlank
        String backImgUrl,

        @NotBlank
        String profileImgUrl
) {
    public Channel toDomain(){
        return Channel.builder()
                .nickname(nickname)
                .introduction(introduction)
                .backImgUrl(backImgUrl)
                .profileImgUrl(profileImgUrl)
                .build();
    }
}
