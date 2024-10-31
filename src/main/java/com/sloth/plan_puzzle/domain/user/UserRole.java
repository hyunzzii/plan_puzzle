package com.sloth.plan_puzzle.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_USER("user"), ROLE_ADMIN("admin");

    private final String role;

    UserRole(final String role){
        this.role = role;
    }
}
