package com.sloth.plan_puzzle.api.service.user;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.domain.user.User;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.dto.user.UserSignupRequest;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void isDuplicateLoginId(final String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new CustomException(CustomExceptionInfo.DUPLICATE_ID);
        }
    }

    public void createUser(final UserSignupRequest userSignupRequest) {
        final User user = userSignupRequest.toDomain(UserRole.ROLE_USER);
        userRepository.save(user.toEntity(passwordEncoder));
    }

    @Transactional(readOnly = true)
    public UserJpaEntity getEntity(final Long userId){
        return userRepository.getUserById(userId);
    }
}
