package com.sloth.plan_puzzle.api.controller.schedule;

import com.sloth.plan_puzzle.api.service.schedule.UserScheduleFacade;
import com.sloth.plan_puzzle.common.security.jwt.CustomUserDetails;
import com.sloth.plan_puzzle.dto.ListWrapperResponse;
import com.sloth.plan_puzzle.dto.schedule.CreateUserScheduleRequest;
import com.sloth.plan_puzzle.dto.schedule.UpdateUserScheduleRequest;
import com.sloth.plan_puzzle.dto.schedule.UserScheduleResponse;
import com.sloth.plan_puzzle.dto.vote.response.SimpleTimeSlotResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class UserScheduleController {
    private final UserScheduleFacade userScheduleFacade;

    @PostMapping
    public void createSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @RequestBody @Valid CreateUserScheduleRequest request) {
        final Long userId = userDetails.getUserId();
        userScheduleFacade.createSchedule(request, userId);
    }

    @PutMapping("/{scheduleId}")
    public void updateSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable Long scheduleId,
                               @RequestBody @Valid UpdateUserScheduleRequest request) {
        final Long userId = userDetails.getUserId();
        userScheduleFacade.updateSchedule(scheduleId, request, userId);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable Long scheduleId) {
        final Long userId = userDetails.getUserId();
        userScheduleFacade.deleteSchedule(scheduleId, userId);
    }

    @PatchMapping("/{scheduleId}")
    public void updateStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long scheduleId) {
        final Long userId = userDetails.getUserId();
        userScheduleFacade.updateScheduleStatus(scheduleId, userId);
    }

    @GetMapping("/proposals")
    public ListWrapperResponse<UserScheduleResponse> getCandidateSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        final Long userId = userDetails.getUserId();
        return ListWrapperResponse.of(
                UserScheduleResponse.fromDomainList(userScheduleFacade.getCandidateSchedules(userId)));
    }

    @GetMapping("/next")
    public ListWrapperResponse<UserScheduleResponse> getRemainingSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        final Long userId = userDetails.getUserId();
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime tomorrow = now.plusDays(1).toLocalDate().atStartOfDay();
        return ListWrapperResponse.of(
                UserScheduleResponse.fromDomainList(userScheduleFacade.getSchedulesWithinPeriod(now, tomorrow, userId)));
    }

    @GetMapping
    public ListWrapperResponse<UserScheduleResponse> getSchedulesByPeriod(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("start") LocalDate start, @RequestParam("end") LocalDate end) {
        final Long userId = userDetails.getUserId();
        final LocalDateTime startDateTime = start.atStartOfDay();
        final LocalDateTime endDateTime = end.atStartOfDay();
        return ListWrapperResponse.of(
                UserScheduleResponse.fromDomainList(
                        userScheduleFacade.getSchedulesWithinPeriod(startDateTime, endDateTime, userId)));
    }

    @GetMapping("/availability")
    public ListWrapperResponse<SimpleTimeSlotResponse> getAvailableTimes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("start") LocalDate start, @RequestParam("end") LocalDate end,
            @RequestParam("duration") Integer duration) {
        final Long userId = userDetails.getUserId();
        final LocalDateTime startDateTime = start.atStartOfDay();
        final LocalDateTime endDateTime = end.atStartOfDay();
        final List<SimpleTimeSlotResponse> timeSlotResponses = SimpleTimeSlotResponse.fromDomainList(
                userScheduleFacade.getAvailableTimes(startDateTime, endDateTime, duration, userId));
        return ListWrapperResponse.of(timeSlotResponses);
    }
}
