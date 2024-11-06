package com.sloth.plan_puzzle.api.service.schedule.user;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.OVERLAPPING_SCHEDULE;
import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CANDIDATE;
import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CONFIRMED;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.domain.schedule.user.UserSchedule;
import com.sloth.plan_puzzle.dto.schedule.user.CreateUserScheduleRequest;
import com.sloth.plan_puzzle.dto.schedule.user.UpdateUserScheduleRequest;
import com.sloth.plan_puzzle.persistence.entity.schedule.user.UserScheduleJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.schedule.user.UserScheduleRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserScheduleService {
    private final UserScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    //CRUD
    public UserSchedule createSchedule(final CreateUserScheduleRequest scheduleRequest, final Long userId) {
        final UserSchedule schedule = scheduleRequest.toDomain().validateSchedule();
        isAvailableSchedule(schedule.startDateTime(), schedule.endDateTime(), userId);    //검증

        final UserJpaEntity userEntity = userRepository.getUserById(userId);
        final UserScheduleJpaEntity scheduleEntity = schedule.toEntity(userEntity);
        scheduleRepository.save(scheduleEntity);
        return UserSchedule.fromEntity(scheduleEntity);
    }

    public void updateSchedule(final Long scheduleId, final UpdateUserScheduleRequest scheduleRequest,
                               final Long userId) {
        final UserSchedule schedule = scheduleRequest.toDomain().validateSchedule();
        isAvailableScheduleForUpdate(scheduleId, schedule.startDateTime(), schedule.endDateTime(), userId);    //검증

        final UserScheduleJpaEntity scheduleEntity = scheduleRepository.getScheduleByIdAndUserId(scheduleId, userId);
        scheduleEntity.update(schedule);
        scheduleRepository.save(scheduleEntity);
    }

    public void deleteSchedule(final Long scheduleId, final Long userId) {
        scheduleRepository.deleteScheduleById(scheduleId, userId);
    }

    public void updateScheduleStatus(final Long scheduleId, final Long userId) {
        final UserScheduleJpaEntity scheduleEntity = scheduleRepository.getScheduleByIdAndUserId(scheduleId, userId);
        scheduleEntity.toggleStatus();
        scheduleRepository.save(scheduleEntity);
    }

    //조회
    @Transactional(readOnly = true)
    public List<UserSchedule> getSchedulesWithinPeriod(final LocalDateTime startOfPeriod,
                                                       final LocalDateTime endOfPeriod, final Long userId) {
        return scheduleRepository.findByPeriodAndUserId(startOfPeriod, endOfPeriod, CONFIRMED, userId).stream()
                .map(UserSchedule::fromEntity)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<UserSchedule> getCandidateSchedules(final Long userId) {
        return scheduleRepository.findByStateAndUserId(CANDIDATE, userId).stream()
                .map(UserSchedule::fromEntity)
                .toList();
    }

    //검증
    @Transactional(readOnly = true)
    public void isAvailableSchedule(final LocalDateTime startOfPeriod,
                                    final LocalDateTime endOfPeriod, final Long userId) {
        if (!scheduleRepository.findByPeriodAndUserId(startOfPeriod, endOfPeriod, CONFIRMED, userId).isEmpty()) {
            throw new CustomException(OVERLAPPING_SCHEDULE);
        }
    }

    @Transactional(readOnly = true)
    public void isAvailableScheduleForUpdate(final Long scheduleId, final LocalDateTime startDateTime,
                                             final LocalDateTime endDateTime, final Long userId) {
        final List<UserScheduleJpaEntity> schedules = scheduleRepository.findByPeriodAndUserId(
                startDateTime, endDateTime, CONFIRMED, userId);
        if (schedules.size() == 1 && schedules.get(0).getId().equals(scheduleId)) {
            return;
        }
        if (!schedules.isEmpty()) {
            throw new CustomException(OVERLAPPING_SCHEDULE);
        }
    }
}