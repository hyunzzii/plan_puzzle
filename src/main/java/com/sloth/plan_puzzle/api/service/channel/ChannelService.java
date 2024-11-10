package com.sloth.plan_puzzle.api.service.channel;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.DUPLICATE_NICKNAME;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.domain.channel.Channel;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    public Channel create(final Channel channel, final UserJpaEntity userEntity) {
        return Channel.fromEntity(channelRepository.save(channel.toEntity(userEntity)));
    }

    public void delete(final Long channelId, final Long userId) {
        channelRepository.deleteChannelById(channelId, userId);
    }

    public void update(final Long channelId, final Channel channel, final Long userId) {
        final ChannelJpaEntity channelEntity = channelRepository.getChannelByIdAndUserId(channelId, userId);
        channelRepository.save(channelEntity.update(channel));
    }

    public void isDuplicateNickname(final String nickname) {
        if (channelRepository.existsByNickname(nickname)) {
            throw new CustomException(DUPLICATE_NICKNAME);
        }
    }

    public Channel getChannel(final Long channelId) {
        return Channel.fromEntity(channelRepository.getChannelById(channelId));
    }

    public List<Channel> getChannelsByUserId(final Long userId) {
        return channelRepository.findByUserId(userId).stream()
                .map(Channel::fromEntity)
                .toList();
    }

    public Page<Channel> getChannelsByPagingForSearch(final String keyword, final Pageable pageable) {
        return channelRepository.getChannelsForSearch(keyword, pageable)
                .map(Channel::fromEntity);
    }

    public Page<Channel> getChannelsByPaging(final Pageable pageable) {
        return channelRepository.findAll(pageable)
                .map(Channel::fromEntity);
    }

    public void verifyChannel(final Long channelId, final Long userId) {
        channelRepository.existsChannelByIdAndUserId(channelId, userId);
    }

    public ChannelJpaEntity getEntity(final Long channelId) {
        return channelRepository.getChannelById(channelId);
    }
}
