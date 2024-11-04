package com.sloth.plan_puzzle.api.service.schedule.user;

import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CANDIDATE;
import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CONFIRMED;

import com.sloth.plan_puzzle.domain.schedule.user.UserSchedule;
import com.sloth.plan_puzzle.dto.schdule.user.UserScheduleCreateRequest;
import com.sloth.plan_puzzle.dto.schdule.user.UserScheduleUpdateRequest;
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
    public UserSchedule createSchedule(final UserScheduleCreateRequest scheduleRequest, final Long userId) {
        UserSchedule schedule = scheduleRequest.toDomain().validateSchedule();
        UserJpaEntity userEntity = userRepository.getUserById(userId);
        UserScheduleJpaEntity scheduleEntity = schedule.createEntity(userEntity);

        userEntity.addSchedule(scheduleEntity);
        scheduleRepository.save(scheduleEntity);
        return UserSchedule.fromEntity(scheduleEntity);
    }

    public void updateSchedule(final Long scheduleId, final UserScheduleUpdateRequest scheduleRequest,
                               final Long userId) {
        UserSchedule schedule = scheduleRequest.toDomain().validateSchedule();
        UserScheduleJpaEntity scheduleEntity = scheduleRepository.getScheduleByIdAndUserId(scheduleId, userId);
        scheduleEntity.update(schedule);
        scheduleRepository.save(scheduleEntity);
    }

    public void deleteSchedule(final Long scheduleId, final Long userId) {
        scheduleRepository.deleteScheduleById(scheduleId, userId);
    }

    public void updateScheduleStatus(final Long scheduleId, final Long userId) {
        UserScheduleJpaEntity scheduleEntity = scheduleRepository.getScheduleByIdAndUserId(scheduleId, userId);
        scheduleEntity.toggleStatus();
        scheduleRepository.save(scheduleEntity);
    }

    //조회
    @Transactional(readOnly = true)
    public List<UserSchedule> getSchedulesWithinPeriod(final LocalDateTime startOfPeriod,
                                                       final LocalDateTime endOfPeriod, final Long userId) {
        return scheduleRepository.findByPeriodAndUserId(
                        startOfPeriod, endOfPeriod, CONFIRMED, userId)
                .stream()
                .map(UserSchedule::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserSchedule> getCandidateSchedules(final Long userId) {
        return scheduleRepository.findByStateAndUserId(CANDIDATE, userId)
                .stream()
                .map(UserSchedule::fromEntity)
                .toList();
    }
}