package com.sloth.plan_puzzle.api.service.channel;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.dto.channel.ChannelRequest;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public Channel createChannel(final ChannelRequest channelRequest, final Long userId) {
        final UserJpaEntity userEntity = userRepository.getUserById(userId);
        final Channel channel = channelRequest.toDomain().validateImgUrl();
        final ChannelJpaEntity channelEntity = channel.toEntity(userEntity);
//        userEntity.addChannel(channelEntity);
        channelRepository.save(channelEntity);
        return Channel.fromEntity(channelEntity);
    }

    public void deleteChannel(final Long channelId, final Long userId) {
        channelRepository.deleteChannelById(channelId, userId);
    }

    public void updateChannel(final Long channelId, final ChannelRequest request, final Long userId) {
        final Channel channel = request.toDomain().validateImgUrl();
        final ChannelJpaEntity channelEntity = channelRepository.getChannelByIdAndUserId(channelId, userId);
        channelEntity.update(channel);
        channelRepository.save(channelEntity);
    }

    @Transactional(readOnly = true)
    public void isDuplicateNickname(final String nickname) {
        if (channelRepository.existsByNickname(nickname)) {
            throw new CustomException(CustomExceptionInfo.DUPLICATE_NICKNAME);
        }
    }

    @Transactional(readOnly = true)
    public Channel getChannel(final Long channelId) {
        return Channel.fromEntity(channelRepository.getChannelById(channelId));
    }

    @Transactional(readOnly = true)
    public List<Channel> getUserChannels(final Long userId) {
        return channelRepository.findByUserId(userId).stream()
                .map(Channel::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<Channel> getChannelsByPagingForSearch(final String keyword, final Pageable pageable) {
        return channelRepository.getChannelsForSearch(keyword, pageable)
                .map(Channel::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<Channel> getChannelsByPaging(final Pageable pageable) {
        return channelRepository.findAll(pageable)
                .map(Channel::fromEntity);
    }
}
