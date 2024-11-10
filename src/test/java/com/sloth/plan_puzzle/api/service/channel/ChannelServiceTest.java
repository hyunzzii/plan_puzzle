package com.sloth.plan_puzzle.api.service.channel;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {
    @InjectMocks
    private ChannelService channelService;

    @Mock
    private ChannelRepository channelRepository;

    @DisplayName("채널의 닉네임이 중복이 아닙니다.")
    @Test
    void isDuplicateNicknameTest() {
        //given
        String nickname = "라멘";
        when(channelRepository.existsByNickname(anyString())).thenAnswer(invocation ->
                nickname.equals(invocation.getArgument(0)));
        //when, then
        assertThatNoException().isThrownBy(() -> channelService.isDuplicateNickname("하루"));
    }

    @DisplayName("채널의 닉네임이 중복이면 예외가 발생합니다.")
    @Test
    void isDuplicateNicknameFailTest() {
        //given
        String nickname = "라멘";
        when(channelRepository.existsByNickname(anyString())).thenAnswer(invocation ->
                nickname.equals(invocation.getArgument(0)));

        //when, then
        assertThatThrownBy(() -> channelService.isDuplicateNickname(nickname))
                .isInstanceOf(CustomException.class)
                .hasMessage(CustomExceptionInfo.DUPLICATE_NICKNAME.getMessage());
    }
}
