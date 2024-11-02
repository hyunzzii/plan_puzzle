package com.sloth.plan_puzzle.persistence.repository.user;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    default UserJpaEntity getByLoginId(final String loginId) {
        return findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(CustomExceptionInfo.NOT_FOUND_USER));
    }
}
