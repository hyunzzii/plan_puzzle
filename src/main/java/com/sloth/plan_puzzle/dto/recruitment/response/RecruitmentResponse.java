package com.sloth.plan_puzzle.dto.recruitment.response;


import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.dto.channel.SimpleChannelResponse;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentScheduleJpaEntity;
import java.util.List;
import lombok.Builder;

@Builder
public record RecruitmentResponse(
        SimpleRecruitmentResponse recruitmentResponse,
        SimpleChannelResponse authorResponse,
        List<RecruitmentScheduleResponse> scheduleResponse
) {
    public static RecruitmentResponse fromEntity(final RecruitmentJpaEntity recruitmentEntity,
                                                 final ChannelJpaEntity channelEntity,
                                                 final List<RecruitmentScheduleJpaEntity> scheduleEntityList) {
        return RecruitmentResponse.builder()
                .recruitmentResponse(SimpleRecruitmentResponse.fromEntity(recruitmentEntity))
                .authorResponse(SimpleChannelResponse.fromDomain(Channel.fromEntity(channelEntity)))
                .scheduleResponse(RecruitmentScheduleResponse.fromEntityList(scheduleEntityList))
                .build();
    }
}
