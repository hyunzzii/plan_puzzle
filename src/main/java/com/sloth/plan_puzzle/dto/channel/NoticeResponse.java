package com.sloth.plan_puzzle.dto.channel;

import com.sloth.plan_puzzle.domain.channel.Notice;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;

@Builder
public record NoticeResponse(
        Long id,

        String title,

        String content,

        String imgUrl
){
        public static List<NoticeResponse> fromEntityList(final List<Notice> notices){
                return notices.stream()
                        .map(NoticeResponse::fromEntity)
                        .toList();
        }
        public static NoticeResponse fromEntity(final Notice notice){
                return NoticeResponse.builder()
                        .id(notice.id())
                        .title(notice.title())
                        .content(notice.content())
                        .imgUrl(notice.imgUrl())
                        .build();
        }
}
