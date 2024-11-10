package com.sloth.plan_puzzle.api.service.channel;

import com.sloth.plan_puzzle.domain.channel.Notice;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.channel.NoticeJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.NoticeRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public Notice create(final Notice notice, final ChannelJpaEntity channelEntity) {
        final NoticeJpaEntity noticeEntity = notice.toEntity(channelEntity);
        noticeRepository.save(noticeEntity);
        return Notice.fromEntity(noticeEntity);
    }

    public void delete(final Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }

    public Page<Notice> getNoticesByPaging(final Long channelId, final Pageable pageable) {
        return noticeRepository.findByChannelId(channelId, pageable)
                .map(Notice::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<Notice> getRecentNoticesForSubscription(final Long channelId, final LocalDateTime recent) {
        return noticeRepository.findRecentNoticesBySubscriberId(channelId, recent).stream()
                .map(Notice::fromEntity)
                .toList();
    }
}
