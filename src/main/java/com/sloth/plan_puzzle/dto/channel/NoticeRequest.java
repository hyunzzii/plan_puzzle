package com.sloth.plan_puzzle.dto.channel;

import com.sloth.plan_puzzle.domain.channel.Notice;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record NoticeRequest(
        @NotBlank
        String title,

        @NotBlank
        String content,

        @NotBlank
        String imgUrl
) {
    public Notice toDomain(){
        return Notice.builder()
                .title(title)
                .content(content)
                .imgUrl(imgUrl)
                .build();
    }
}
