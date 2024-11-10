package com.sloth.plan_puzzle.api.service.schedule;

import com.sloth.plan_puzzle.api.service.user.UserService;
import com.sloth.plan_puzzle.domain.schedule.UserSchedule;
import com.sloth.plan_puzzle.domain.vote.TimeSlot;
import com.sloth.plan_puzzle.dto.schedule.CreateUserScheduleRequest;
import com.sloth.plan_puzzle.dto.schedule.UpdateUserScheduleRequest;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserScheduleFacade {
    private final UserScheduleService userScheduleService;
    private final UserService userService;

    //CRUD
    public UserSchedule createSchedule(final CreateUserScheduleRequest scheduleRequest, final Long userId) {
        final UserSchedule schedule = scheduleRequest.toDomain().validateSchedule();
        final UserJpaEntity userEntity = userService.getEntity(userId);
        return userScheduleService.create(schedule, userEntity);
    }

    public void updateSchedule(final Long scheduleId, final UpdateUserScheduleRequest scheduleRequest,
                               final Long userId) {
        final UserSchedule schedule = scheduleRequest.toDomain().validateSchedule();
        userScheduleService.update(schedule, userId);
    }

    public void deleteSchedule(final Long scheduleId, final Long userId) {
        userScheduleService.delete(scheduleId, userId);
    }

    public void updateScheduleStatus(final Long scheduleId, final Long userId) {
        userScheduleService.updateStatus(scheduleId, userId);
    }

    //조회
    @Transactional(readOnly = true)
    public List<UserSchedule> getSchedulesWithinPeriod(final LocalDateTime startOfPeriod,
                                                       final LocalDateTime endOfPeriod, final Long userId) {
        return userScheduleService.getWithinPeriod(startOfPeriod, endOfPeriod, userId);
    }


    @Transactional(readOnly = true)
    public List<UserSchedule> getCandidateSchedules(final Long userId) {
        return userScheduleService.getCandidates(userId);
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getAvailableTimes(final LocalDateTime start, final LocalDateTime end,
                                            final Integer duration, final Long userId) {
        return userScheduleService.getAvailableTimes(start, end, duration, userId);
    }
}
