package com.sloth.plan_puzzle.common.exception;

import lombok.Builder;

@Builder
public record ErrorResponseBody(
        int detailStatusCode,
        String message
) {
}