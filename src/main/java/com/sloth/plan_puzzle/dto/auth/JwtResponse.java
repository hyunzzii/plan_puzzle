package com.sloth.plan_puzzle.dto.auth;

import lombok.Builder;

@Builder
public record JwtResponse(
        String grantType,
        String accessToken,
        String refreshToken
) {

}
