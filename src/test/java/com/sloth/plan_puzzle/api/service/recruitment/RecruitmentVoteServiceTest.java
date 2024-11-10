package com.sloth.plan_puzzle.api.service.recruitment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.ChannelVoteJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.TimeSlotJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.vote.ChannelVoteRepository;
import com.sloth.plan_puzzle.persistence.repository.vote.TimeSlotRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecruitmentVoteServiceTest {
    @InjectMocks
    private RecruitmentVoteService voteService;

    @Mock
    private ChannelVoteRepository channelVoteRepository;
    @Mock
    private TimeSlotRepository timeSlotRepository;

    private ChannelJpaEntity channelEntity;
    private final Long channelId = 1L;

    @BeforeEach
    void setUp() {
        channelEntity = ChannelJpaEntity.builder().build();
    }

    @DisplayName("투표 이력이 있으면 초기화 된 후 투표를 다시 반영합니다.")
    @Test
    void voteTest() {
        // given
        List<Long> timeSlotIds = List.of(1L, 2L, 3L);
        List<TimeSlotJpaEntity> timeSlotEntities = new ArrayList<>();
        for (int i = 0; i < timeSlotIds.size(); i++) {
            Long timeSlotId = timeSlotIds.get(i);
            TimeSlotJpaEntity entity = TimeSlotJpaEntity.builder().build();
            timeSlotEntities.add(entity);
            when(timeSlotRepository.getTimeSlotById(timeSlotId))
                    .thenReturn(entity);
        }

        // when
        voteService.vote(channelId, 10L, timeSlotIds, channelEntity);

        // then
        verify(channelVoteRepository).deleteForVote(10L, channelId);

        ArgumentCaptor<ChannelVoteJpaEntity> captor = ArgumentCaptor.forClass(ChannelVoteJpaEntity.class);
        verify(channelVoteRepository, times(timeSlotIds.size())).save(captor.capture());

        List<ChannelVoteJpaEntity> savedEntities = captor.getAllValues();
        for (int i = 0; i < timeSlotIds.size(); i++) {
            TimeSlotJpaEntity expectedTimeSlot = timeSlotEntities.get(i);
            ChannelVoteJpaEntity savedEntity = savedEntities.get(i);

            assertThat(savedEntity.getTimeSlot()).isEqualTo(expectedTimeSlot);
            assertThat(savedEntity.getChannel()).isEqualTo(channelEntity);
        }
    }
}