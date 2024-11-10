package com.sloth.plan_puzzle.api.service.schedule;

import static com.sloth.plan_puzzle.common.validator.TimeValidator.TimeFormatUnit;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.OVERLAPPING_SCHEDULE;
import static com.sloth.plan_puzzle.domain.schedule.UserScheduleState.CANDIDATE;
import static com.sloth.plan_puzzle.domain.schedule.UserScheduleState.CONFIRMED;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.domain.schedule.UserSchedule;
import com.sloth.plan_puzzle.domain.schedule.UserScheduleState;
import com.sloth.plan_puzzle.domain.vote.TimeSlot;
import com.sloth.plan_puzzle.persistence.entity.schedule.UserScheduleJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.schedule.UserScheduleRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserScheduleService {
    private final UserScheduleRepository userScheduleRepository;

    //CRUD
    public UserSchedule create(final UserSchedule schedule, final UserJpaEntity userEntity) {
        if (userScheduleRepository.existsByPeriodAndUserId(schedule.startDateTime(), schedule.endDateTime(),
                UserScheduleState.CONFIRMED, userEntity.getId())) {
            throw new CustomException(OVERLAPPING_SCHEDULE);
        }
        final UserScheduleJpaEntity scheduleEntity = schedule.toEntity(userEntity);
        userScheduleRepository.save(scheduleEntity);
        return UserSchedule.fromEntity(scheduleEntity);
    }

    public void update(final UserSchedule schedule, final Long userId) {
        if (!isAvailableScheduleForUpdate(schedule, userId)) {
            throw new CustomException(OVERLAPPING_SCHEDULE);
        }
        final UserScheduleJpaEntity scheduleEntity = userScheduleRepository.getScheduleByIdAndUserId(schedule.id(),
                userId);
        scheduleEntity.update(schedule);
        userScheduleRepository.save(scheduleEntity);
    }

    public void delete(final Long scheduleId, final Long userId) {
        userScheduleRepository.deleteScheduleByIdAndUserId(scheduleId, userId);
    }

    public void updateStatus(final Long scheduleId, final Long userId) {
        final UserScheduleJpaEntity scheduleEntity = userScheduleRepository.getScheduleByIdAndUserId(scheduleId,
                userId);
        scheduleEntity.toggleStatus();
        userScheduleRepository.save(scheduleEntity);
    }

    public List<UserSchedule> getWithinPeriod(final LocalDateTime startOfPeriod,
                                              final LocalDateTime endOfPeriod, final Long userId) {
        return userScheduleRepository.findByPeriodAndUserId(startOfPeriod, endOfPeriod, CONFIRMED, userId).stream()
                .map(UserSchedule::fromEntity)
                .toList();
    }

    public List<UserSchedule> getCandidates(final Long userId) {
        return userScheduleRepository.findByStateAndUserId(CANDIDATE, userId).stream()
                .map(UserSchedule::fromEntity)
                .toList();
    }

    public List<TimeSlot> getAvailableTimes(final LocalDateTime startDateTime, final LocalDateTime endDateTime,
                                            final Integer duration, final Long userId) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        List<UserScheduleJpaEntity> schedules = userScheduleRepository.findByPeriodAndUserId(
                startDateTime, endDateTime, CONFIRMED, userId);

        LocalDateTime currentTime = startDateTime;
        int index = 0;
        while (!currentTime.plusMinutes(duration).isAfter(endDateTime)) {
            boolean isAvailable = true;
            LocalDateTime slotEndTime = currentTime.plusMinutes(duration);
            for (int i = index; i < schedules.size(); i++) {
                UserScheduleJpaEntity schedule = schedules.get(i);

                if (!(currentTime.isAfter(schedule.getEndDateTime())
                        || !slotEndTime.isAfter(schedule.getStartDateTime()))) {
                    currentTime = schedule.getEndDateTime();
                    isAvailable = false;
                    index++;
                    break;
                }
            }
            if (isAvailable) {
                timeSlots.add(TimeSlot.create(currentTime, slotEndTime));
                currentTime = currentTime.plusMinutes(TimeFormatUnit);
            }
        }
        return timeSlots;
    }

    public boolean isAvailableSchedule(final TimeSlot timeSlot, final Long userId) {
        final List<UserScheduleJpaEntity> schedules = userScheduleRepository.findByPeriodAndUserId(
                timeSlot.startDateTime(), timeSlot.endDateTime(), CONFIRMED, userId);
        return schedules.isEmpty();
    }

    private boolean isAvailableScheduleForUpdate(final UserSchedule schedule, final Long userId) {
        final List<UserScheduleJpaEntity> schedules = userScheduleRepository.findByPeriodAndUserId(
                schedule.startDateTime(), schedule.endDateTime(), CONFIRMED, userId);
        if (schedules.size() == 1 && schedules.get(0).getId().equals(schedule.id())) {
            return true;
        }
        return schedules.isEmpty();
    }
}