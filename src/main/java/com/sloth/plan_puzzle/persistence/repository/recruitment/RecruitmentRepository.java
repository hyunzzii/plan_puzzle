package com.sloth.plan_puzzle.persistence.repository.recruitment;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.NOT_FOUND_RECRUITMENT;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.UNAUTHORIZED_ACCESS;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentRepository extends JpaRepository<RecruitmentJpaEntity, Long> {

    Page<RecruitmentJpaEntity> findByTitleContainingOrContentContainingOrRegion_ProvinceContainingOrRegion_CityContaining(
            String titleKeyword, String contentKeyword, String provinceKeyword, String cityKeyword, Pageable pageable);

    @Query("SELECT r FROM RecruitmentJpaEntity r WHERE r.author.id = :authorId")
    Page<RecruitmentJpaEntity> findByAuthorId(@Param("authorId") Long authorId,
                                              Pageable pageable);

    @Query("SELECT r FROM RecruitmentJpaEntity r WHERE r.id = :id AND r.author.id = :authorId")
    Optional<RecruitmentJpaEntity> findByIdAndAuthorId(@Param("id") Long id,
                                                       @Param("authorId") Long authorId);

    default RecruitmentJpaEntity getRecruitmentByIdAndAuthorId(final Long id, final Long authorId) {
        return findByIdAndAuthorId(id, authorId)
                .orElseThrow(() -> new CustomException(UNAUTHORIZED_ACCESS));
    }

    default RecruitmentJpaEntity getRecruitmentById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RECRUITMENT));
    }

    default Page<RecruitmentJpaEntity> getRecruitmentListForSearch(final String keyword, final Pageable pageable) {
        return findByTitleContainingOrContentContainingOrRegion_ProvinceContainingOrRegion_CityContaining(keyword,
                keyword, keyword, keyword, pageable);
    }
}
