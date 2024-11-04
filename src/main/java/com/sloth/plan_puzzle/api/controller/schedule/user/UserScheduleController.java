package com.sloth.plan_puzzle.api.controller.schedule.user;

import com.sloth.plan_puzzle.api.service.schedule.user.UserScheduleService;
import com.sloth.plan_puzzle.common.security.jwt.CustomUserDetails;
import com.sloth.plan_puzzle.dto.ListWrapperResponse;
import com.sloth.plan_puzzle.dto.schdule.user.UserScheduleCreateRequest;
import com.sloth.plan_puzzle.dto.schdule.user.UserScheduleIdResponse;
import com.sloth.plan_puzzle.dto.schdule.user.UserScheduleResponse;
import com.sloth.plan_puzzle.dto.schdule.user.UserScheduleUpdateRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final UserScheduleService scheduleService;

    @PostMapping
    public UserScheduleIdResponse createSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestBody @Valid UserScheduleCreateRequest request) {
        final Long userId = userDetails.getUserId();
        return UserScheduleIdResponse.fromDomain(scheduleService.createSchedule(request, userId));
    }

    @PutMapping("/{scheduleId}")
    public void updateSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable Long scheduleId,
                               @RequestBody @Valid UserScheduleUpdateRequest request) {
        final Long userId = userDetails.getUserId();
        scheduleService.updateSchedule(scheduleId, request, userId);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @PathVariable Long scheduleId) {
        final Long userId = userDetails.getUserId();
        scheduleService.deleteSchedule(scheduleId, userId);
    }

    @PatchMapping("/{scheduleId}")
    public void updateStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable Long scheduleId) {
        final Long userId = userDetails.getUserId();
        scheduleService.updateScheduleStatus(scheduleId, userId);
    }

    @GetMapping("/proposals")
    public ListWrapperResponse<UserScheduleResponse> getCandidateSchedules(@AuthenticationPrincipal CustomUserDetails userDetails){
        final Long userId = userDetails.getUserId();
        return ListWrapperResponse.of(
                UserScheduleResponse.fromDomainList(scheduleService.getCandidateSchedules(userId)));
    }

    @GetMapping("/next")
    public ListWrapperResponse<UserScheduleResponse> getRemainingSchedules(@AuthenticationPrincipal CustomUserDetails userDetails){
        final Long userId = userDetails.getUserId();
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime tomorrow = now.plusDays(1).toLocalDate().atStartOfDay();
        return ListWrapperResponse.of(
                UserScheduleResponse.fromDomainList(scheduleService.getSchedulesWithinPeriod(now, tomorrow, userId)));
    }

    @GetMapping
    public ListWrapperResponse<UserScheduleResponse> getSchedulesByPeriod(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                          @RequestParam("start") LocalDate start, @RequestParam("end") LocalDate end){
        final Long userId = userDetails.getUserId();
        final LocalDateTime startDateTime = start.atStartOfDay();
        final LocalDateTime endDateTime =  end.atStartOfDay();
        return ListWrapperResponse.of(
                UserScheduleResponse.fromDomainList(scheduleService.getSchedulesWithinPeriod(startDateTime,endDateTime,userId)));
    }
}
