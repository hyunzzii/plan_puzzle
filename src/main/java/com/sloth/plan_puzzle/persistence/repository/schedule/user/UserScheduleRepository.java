package com.sloth.plan_puzzle.persistence.repository.schedule.user;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState;
import com.sloth.plan_puzzle.persistence.entity.schedule.user.UserScheduleJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserScheduleRepository extends JpaRepository<UserScheduleJpaEntity, Long> {

    @Query("SELECT s FROM UserScheduleJpaEntity s WHERE s.id = :scheduleId AND s.user.id = :userId")
    Optional<UserScheduleJpaEntity> findByScheduleIdAndUserId(
            @Param("scheduleId") Long scheduleId, @Param("userId") Long userId);


    @Query("SELECT s FROM UserScheduleJpaEntity s WHERE s.user.id = :userId AND "
            + "(s.startDateTime >= :startDateTime AND s.startDateTime < :endDateTime)"
            + "OR (s.endDateTime > :startDateTime AND s.endDateTime <= : endDateTime)"
            + "AND s.state = :state")
    List<UserScheduleJpaEntity> findByPeriodAndUserId(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("state") UserScheduleState state,
            @Param("userId") Long userId);

    @Query("SELECT s FROM UserScheduleJpaEntity s WHERE s.state = :state AND s.user.id = :userId")
    List<UserScheduleJpaEntity> findByStateAndUserId(UserScheduleState state, Long userId);

    //default는 getEntity~
    default UserScheduleJpaEntity getScheduleById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(CustomExceptionInfo.NOT_FOUND_SCHEDULE));
    }

    default boolean existsScheduleByIdAndUserId(final Long id, final Long userId) {
        findByScheduleIdAndUserId(id, userId).orElseThrow(
                () -> new CustomException(CustomExceptionInfo.NOT_FOUND_SCHEDULE)
        );
        return true;
    }

    default UserScheduleJpaEntity getScheduleByIdAndUserId(final Long id, final Long userId) {
        return findByScheduleIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(CustomExceptionInfo.NOT_FOUND_SCHEDULE));
    }

    default void deleteScheduleById(final Long id, final Long userId) {
        if (!existsScheduleByIdAndUserId(id, userId)) {
            throw new CustomException(CustomExceptionInfo.NOT_FOUND_SCHEDULE);
        }
        deleteById(id);
    }
}
