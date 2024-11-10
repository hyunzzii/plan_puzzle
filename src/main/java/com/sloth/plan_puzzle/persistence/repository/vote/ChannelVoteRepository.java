package com.sloth.plan_puzzle.persistence.repository.vote;

import com.sloth.plan_puzzle.persistence.entity.vote.ChannelVoteJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelVoteRepository extends JpaRepository<ChannelVoteJpaEntity, Long> {

    @Query("SELECT c FROM ChannelVoteJpaEntity c WHERE c.timeSlot.id = :timeSlotId")
    List<ChannelVoteJpaEntity> findListByTimeSlotId(@Param("timeSlotId") Long timeSlotId);

    @Query("SELECT c FROM ChannelVoteJpaEntity c WHERE c.timeSlot.vote.id = :voteId AND c.channel.id = :channelId")
    List<ChannelVoteJpaEntity> findListByVoteIdAndChannelId(@Param("voteId") Long voteId,
                                                            @Param("channelId") Long channelId);

    default void deleteForVote(final Long voteId, final Long channelId) {
        this.deleteAll(findListByVoteIdAndChannelId(voteId, channelId));
    }
}
