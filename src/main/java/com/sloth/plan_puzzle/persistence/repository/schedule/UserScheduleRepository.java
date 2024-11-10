package com.sloth.plan_puzzle.persistence.repository.schedule;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.NOT_FOUND_SCHEDULE;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.UNAUTHORIZED_ACCESS;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.domain.schedule.UserScheduleState;
import com.sloth.plan_puzzle.persistence.entity.schedule.UserScheduleJpaEntity;
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
            + "((s.startDateTime >= :startDateTime AND s.startDateTime < :endDateTime) "
            + "OR (s.endDateTime > :startDateTime AND s.endDateTime <= :endDateTime)) "
            + "AND s.state = :state "
            + "ORDER BY s.startDateTime ASC")
    List<UserScheduleJpaEntity> findByPeriodAndUserId(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("state") UserScheduleState state,
            @Param("userId") Long userId);


    @Query("SELECT COUNT(s) > 0 FROM UserScheduleJpaEntity s WHERE s.user.id = :userId AND "
            + "((s.startDateTime >= :startDateTime AND s.startDateTime < :endDateTime) "
            + "OR (s.endDateTime > :startDateTime AND s.endDateTime <= :endDateTime)) "
            + "AND s.state = :state ")
    boolean existsByPeriodAndUserId(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("state") UserScheduleState state,
            @Param("userId") Long userId);


    @Query("SELECT s FROM UserScheduleJpaEntity s WHERE s.state = :state AND s.user.id = :userId")
    List<UserScheduleJpaEntity> findByStateAndUserId(UserScheduleState state, Long userId);

    //default는 getEntity~
    default UserScheduleJpaEntity getScheduleById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_SCHEDULE));
    }

    default boolean existsScheduleByIdAndUserId(final Long id, final Long userId) {
        findByScheduleIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(UNAUTHORIZED_ACCESS));
        return true;
    }

    default UserScheduleJpaEntity getScheduleByIdAndUserId(final Long id, final Long userId) {
        return findByScheduleIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(UNAUTHORIZED_ACCESS));
    }

    default void deleteScheduleByIdAndUserId(final Long id, Long userId) {
        if (!existsScheduleByIdAndUserId(id,userId)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }
        deleteById(id);
    }
}
