package com.sloth.plan_puzzle.dto.auth;

import lombok.Builder;

@Builder
public record LoginRequest(
        String loginId,
        String loginPw
) {

}