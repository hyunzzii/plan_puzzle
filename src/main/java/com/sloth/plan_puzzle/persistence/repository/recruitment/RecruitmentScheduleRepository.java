package com.sloth.plan_puzzle.persistence.repository.recruitment;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentScheduleJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentScheduleRepository extends JpaRepository<RecruitmentScheduleJpaEntity, Long> {

    @Query("SELECT s FROM RecruitmentScheduleJpaEntity s "
            + "WHERE s.recruitment.id = :recruitmentId "
            + "ORDER BY s.startDateTime ASC")
    List<RecruitmentScheduleJpaEntity> findByRecruitmentId(@Param("recruitmentI") Long recruitmentId);

    default RecruitmentScheduleJpaEntity getScheduleById(final Long id) {
        return findById(id).orElseThrow(() -> new CustomException(CustomExceptionInfo.NOT_FOUND_SCHEDULE));
    }

    default void deleteScheduleById(final Long scheduleId) {
        delete(getScheduleById(scheduleId));
    }
}
