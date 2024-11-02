package com.sloth.plan_puzzle.domain.user;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
public record User(
        String loginId,
        String loginPw,
        String name,
        String email,
        AgeGroup ageGroup,
        Gender gender,
        UserRole role

) {
    public static User toDomain(final UserJpaEntity userJpaEntity){
        return User.builder()
                .loginId(userJpaEntity.getLoginId())
                .loginPw(userJpaEntity.getLoginPw())
                .name(userJpaEntity.getName())
                .email(userJpaEntity.getEmail())
                .ageGroup(userJpaEntity.getAgeGroup())
                .gender(userJpaEntity.getGender())
                .role(userJpaEntity.getRole())
                .build();
    }

    public UserJpaEntity createSignupUserEntity(final PasswordEncoder passwordEncoder) {
        return UserJpaEntity.builder()
                .loginId(loginId)
                .loginPw(passwordEncoder.encode(loginPw))
                .name(name)
                .email(email)
                .ageGroup(ageGroup)
                .gender(gender)
                .role(role)
                .build();
    }

    public void validatePassword(final PasswordEncoder passwordEncoder, final String loginPw){
        if(!passwordEncoder.matches(loginPw,this.loginPw)){
            throw new CustomException(CustomExceptionInfo.INVALID_PASSWORD);
        }
    }
}
