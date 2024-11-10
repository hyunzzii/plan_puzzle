package com.sloth.plan_puzzle.persistence.repository.vote;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.*;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.persistence.entity.vote.TimeSlotJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TimeSlotRepository extends JpaRepository<TimeSlotJpaEntity, Long> {
    @Query("SELECT t FROM TimeSlotJpaEntity t WHERE t.vote.id = :voteId "
            + "ORDER BY t.startDateTime")
    List<TimeSlotJpaEntity> findListByVoteId(@Param("voteId") Long voteId);

    default TimeSlotJpaEntity getTimeSlotById(final Long id){
        return findById(id)
                .orElseThrow(()->new CustomException(NOT_FOUND_TIMESLOT));
    }
}
