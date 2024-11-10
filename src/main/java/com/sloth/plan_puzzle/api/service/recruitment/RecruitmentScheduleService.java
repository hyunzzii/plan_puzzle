package com.sloth.plan_puzzle.api.service.recruitment;

import com.sloth.plan_puzzle.domain.recruitment.RecruitmentSchedule;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.recruitment.RecruitmentScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitmentScheduleService {
    private final RecruitmentScheduleRepository recruitmentScheduleRepository;

    public RecruitmentSchedule getSchedule(final Long scheduleId) {
        return RecruitmentSchedule.fromEntity(recruitmentScheduleRepository.getScheduleById(scheduleId));
    }

    public void create(final RecruitmentSchedule recruitmentSchedule,
                       final RecruitmentJpaEntity recruitmentEntity) {
        recruitmentScheduleRepository.save(recruitmentSchedule.toEntity(recruitmentEntity));
    }

    public void delete(final Long scheduleId) {
        recruitmentScheduleRepository.deleteScheduleById(scheduleId);
    }


    //주인만 생성 가능
    public List<RecruitmentSchedule> getRecruitmentSchedules(final Long recruitmentId) {
        return recruitmentScheduleRepository
                .findByRecruitmentId(recruitmentId).stream().map(RecruitmentSchedule::fromEntity).toList();
    }
}
