package com.sloth.plan_puzzle.api.service.recruitment;

import com.sloth.plan_puzzle.domain.recruitment.RecruitState;
import com.sloth.plan_puzzle.domain.recruitment.Recruitment;
import com.sloth.plan_puzzle.dto.recruitment.response.RecruitmentResponse;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.recruitment.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;

    public Recruitment create(final Recruitment recruitment, final ChannelJpaEntity channelEntity){
        final RecruitmentJpaEntity recruitmentEntity = recruitment.toEntity(channelEntity);
        recruitmentRepository.save(recruitmentEntity);
        return Recruitment.fromEntity(recruitmentEntity);
    }

    public void update(final Recruitment recruitment, final Long recruitmentId) {
        final RecruitmentJpaEntity recruitmentEntity = recruitmentRepository.getRecruitmentById(recruitmentId);
        final Integer participationSize = recruitmentEntity.getParticipationList().size();
        recruitment.validateRecruitCapacityForUpdate(participationSize);
        recruitmentRepository.save(recruitmentEntity.update(recruitment.title(),recruitment.content(),
                recruitment.recruitCapacity(),recruitment.imgUrl(),recruitment.region()));
    }

    public void deleteById(final Long recruitmentId) {
        recruitmentRepository.deleteById(recruitmentId);
    }

    public void patchState(final Long recruitmentId,final RecruitState state) {
        final RecruitmentJpaEntity recruitmentEntity = recruitmentRepository.getRecruitmentById(recruitmentId);
        recruitmentRepository.save(recruitmentEntity.patchState(state));
    }

    public RecruitmentResponse getRecruitment(final Long recruitmentId) {
        final RecruitmentJpaEntity recruitmentEntity = recruitmentRepository.getRecruitmentById(recruitmentId);
        return RecruitmentResponse.fromEntity(
                recruitmentEntity, recruitmentEntity.getAuthor(), recruitmentEntity.getSchedules());
    }

    public Page<RecruitmentResponse> getChannelRecruitments(final Long channelId, final Pageable pageable) {
        final Page<RecruitmentJpaEntity> recruitmentEntityList = recruitmentRepository
                .findByAuthorId(channelId, pageable);
        return recruitmentEntityList.map(recruitmentEntity -> RecruitmentResponse.fromEntity(
                recruitmentEntity, recruitmentEntity.getAuthor(), recruitmentEntity.getSchedules()));
    }

    public Page<RecruitmentResponse> getRecruitmentList(final Pageable pageable) {
        final Page<RecruitmentJpaEntity> recruitmentEntityList = recruitmentRepository.findAll(pageable);
        return recruitmentEntityList.map(recruitmentEntity -> RecruitmentResponse.fromEntity(
                recruitmentEntity, recruitmentEntity.getAuthor(), recruitmentEntity.getSchedules()));
    }

    public Page<RecruitmentResponse> getRecruitmentListForSearch(final String keyword, final Pageable pageable) {
        final Page<RecruitmentJpaEntity> recruitmentEntityList = recruitmentRepository
                .getRecruitmentListForSearch(keyword, pageable);
        return recruitmentEntityList.map(recruitmentEntity -> RecruitmentResponse.fromEntity(
                recruitmentEntity, recruitmentEntity.getAuthor(), recruitmentEntity.getSchedules()));
    }


    public void verifyAuthor(final Long recruitmentId, final Long channelId) {
        recruitmentRepository.getRecruitmentByIdAndAuthorId(recruitmentId, channelId);
    }

    public RecruitmentJpaEntity getEntity(final Long id){
        return recruitmentRepository.getRecruitmentById(id);
    }

}