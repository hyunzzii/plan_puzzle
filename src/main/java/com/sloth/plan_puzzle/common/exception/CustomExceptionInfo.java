package com.sloth.plan_puzzle.common.exception;

import lombok.Getter;

@Getter
public enum CustomExceptionInfo {
    //401
    //jwt
    INVALID_TOKEN(401, "invalid token", 401001),
    EXPIRED_TOKEN(401, "expired token", 401002),
    LOGOUT_TOKEN(401, "user is logged out", 401003);

    private final int statusCode;
    private final String message;
    private final int detailStatusCode;

    CustomExceptionInfo(int statusCode, String message, int detailStatusCode) {
        this.statusCode = statusCode;
        this.message = message;
        this.detailStatusCode = detailStatusCode;
    }
}
