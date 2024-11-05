package com.sloth.plan_puzzle.persistence.repository.channel;

import com.sloth.plan_puzzle.persistence.entity.channel.NoticeJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<NoticeJpaEntity, Long> {
    @Query("SELECT n FROM NoticeJpaEntity n WHERE n.channel.id = :channelId")
    Page<NoticeJpaEntity> findByChannelId(@Param("channelId") Long channelId, Pageable pageable);

    @Query("SELECT n FROM NoticeJpaEntity n " +
            "JOIN SubscriptionJpaEntity s ON s.subscribed.id = n.channel.id " +
            "WHERE s.subscriber.id = :subscriberId " +
            "AND n.createdDate >= :oneWeekAgo " +
            "ORDER BY n.createdDate DESC")
    List<NoticeJpaEntity> findRecentNoticesBySubscriberId(
            @Param("subscriberId") Long subscriberId,
            @Param("oneWeekAgo") LocalDateTime oneWeekAgo);
}
