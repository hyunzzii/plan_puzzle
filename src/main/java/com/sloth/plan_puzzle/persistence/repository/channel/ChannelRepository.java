package com.sloth.plan_puzzle.persistence.repository.channel;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.NOT_FOUND_CHANNEL;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.UNAUTHORIZED_ACCESS;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<ChannelJpaEntity, Long> {
    boolean existsByNickname(String nickname);

    Page<ChannelJpaEntity> findByNicknameContainingOrIntroductionContaining(
            String nicknameKeyword, String introductionKeyword, Pageable pageable);

    @Query("SELECT c FROM ChannelJpaEntity c WHERE c.user.id = :userId")
    List<ChannelJpaEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM ChannelJpaEntity c WHERE c.id = :id AND c.user.id = :userId")
    Optional<ChannelJpaEntity> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT COUNT(c) > 0 FROM ChannelJpaEntity c WHERE c.id = :id AND c.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    default void existsChannelByIdAndUserId(final Long id, final Long userId) {
        if(!existsByIdAndUserId(id, userId)){
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }
    }

    default ChannelJpaEntity getChannelById(final Long id) {
        return findById(id).orElseThrow(() ->
                new CustomException(NOT_FOUND_CHANNEL));
    }

    default ChannelJpaEntity getChannelByIdAndUserId(final Long id, final Long userId) {
        return findByIdAndUserId(id, userId).orElseThrow(() ->
                new CustomException(UNAUTHORIZED_ACCESS));
    }

    default void deleteChannelById(final Long id, final Long userId) {
        if (!existsByIdAndUserId(id, userId)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }
        deleteById(id);
    }

    default Page<ChannelJpaEntity> getChannelsForSearch(
            final String keyword, final Pageable pageable) {
        return findByNicknameContainingOrIntroductionContaining(keyword, keyword, pageable);
    }
}
