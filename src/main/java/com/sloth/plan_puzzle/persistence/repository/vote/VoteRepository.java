package com.sloth.plan_puzzle.persistence.repository.vote;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.NOT_FOUND_VOTE;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.vote.VoteJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteRepository extends JpaRepository<VoteJpaEntity, Long> {
    @Query("SELECT v FROM VoteJpaEntity v WHERE v.recruitment.id = :recruitmentId")
    Optional<VoteJpaEntity> findByRecruitmentId(@Param("recruitmentId") Long recruitmentId);

//    @Query("SELECT v FROM VoteJpaEntity v WHERE v.id = :id AND v.recruitment.id = :recruitmentId")
//    Optional<VoteJpaEntity> findByIdAndRecruitmentId(@Param("id") Long id,
//                                                     @Param("recruitmentId") Long recruitmentId);

    default VoteJpaEntity getVoteByRecruitmentId(final Long recruitmentId) {
        return findByRecruitmentId(recruitmentId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_VOTE));
    }

    default Boolean existsVoteByRecruitment(final Long recruitmentId) {
        return findByRecruitmentId(recruitmentId).isPresent();
    }

    default void deleteVoteById(final Long id) {
        VoteJpaEntity voteEntity = findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_VOTE));
        delete(voteEntity);
    }
}
