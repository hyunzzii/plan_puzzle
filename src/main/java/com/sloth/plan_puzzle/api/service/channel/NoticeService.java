package com.sloth.plan_puzzle.api.service.channel;

import com.sloth.plan_puzzle.domain.channel.Notice;
import com.sloth.plan_puzzle.dto.channel.NoticeRequest;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.channel.NoticeJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.channel.NoticeRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final ChannelRepository channelRepository;

    public Notice createNotice(final Long channelId, final NoticeRequest request, final Long userId) {
        final ChannelJpaEntity channelEntity = channelRepository.getChannelByIdAndUserId(channelId, userId);
        final Notice notice = request.toDomain().validateImgUrl();
        final NoticeJpaEntity noticeEntity = notice.toEntity(channelEntity);
        noticeRepository.save(noticeEntity);
        return Notice.fromEntity(noticeEntity);
    }

    public void deleteNotice(final Long channelId, final Long noticeId, final Long userId) {
        channelRepository.existsByIdAndUserId(channelId, userId);
        noticeRepository.deleteById(noticeId);
    }

    @Transactional(readOnly = true)
    public Page<Notice> getNoticesByPaging(final Long channelId, final Pageable pageable) {
        return noticeRepository.findByChannelId(channelId, pageable)
                .map(Notice::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<Notice> getRecentNoticesForSubscription(final Long channelId, final Long userId,
                                                        final LocalDateTime recent) {
        channelRepository.existsByIdAndUserId(channelId, userId);
        return noticeRepository.findRecentNoticesBySubscriberId(channelId, recent).stream()
                .map(Notice::fromEntity)
                .toList();
    }
}