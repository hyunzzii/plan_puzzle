package com.sloth.plan_puzzle.domain.user;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
public record User(
        Long id,
        String loginId,
        String loginPw,
        String name,
        String email,
        AgeGroup ageGroup,
        Gender gender,
        UserRole role

) {
    public static User fromEntity(final UserJpaEntity userEntity){
        return User.builder()
                .id(userEntity.getId())
                .loginId(userEntity.getLoginId())
                .loginPw(userEntity.getLoginPw())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .ageGroup(userEntity.getAgeGroup())
                .gender(userEntity.getGender())
                .role(userEntity.getRole())
                .build();
    }

    public UserJpaEntity createEntity(final PasswordEncoder passwordEncoder) {
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
