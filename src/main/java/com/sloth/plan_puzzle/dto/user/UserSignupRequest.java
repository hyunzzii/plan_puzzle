package com.sloth.plan_puzzle.dto.user;

import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.User;
import com.sloth.plan_puzzle.domain.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserSignupRequest(
        @NotBlank
        String loginId,

        @NotBlank
        String loginPw,

        @NotBlank
        String name,

        @NotBlank
        String email,

        @NotNull
        AgeGroup ageGroup,

        @NotNull
        Gender gender
) {
    public User toDomain(UserRole role) {
        return User.builder()
                .loginId(loginId)
                .loginPw(loginPw)
                .name(name)
                .email(email)
                .ageGroup(ageGroup)
                .gender(gender)
                .role(role)
                .build();
    }
}
