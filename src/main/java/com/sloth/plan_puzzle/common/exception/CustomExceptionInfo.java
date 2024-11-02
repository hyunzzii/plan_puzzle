package com.sloth.plan_puzzle.common.exception;

import lombok.Getter;

@Getter
public enum CustomExceptionInfo {
    //401
    //jwt
    INVALID_ACCESS_TOKEN(401, "invalid access token", 401001),
    EXPIRED_ACCESS_TOKEN(401, "expired access token", 401002),
    INVALID_REFRESH_TOKEN(401,"invalid refresh token",401003),

    INVALID_PASSWORD(401,"wrong password",401004),
    //403

    //404
    NOT_FOUND_USER(404,"user not found",404001),

    //409
    DUPLICATE_ID(409,"id already exists",409001),
    DUPLICATE_NICKNAME(409,"nickname already exists",409002);

    private final int statusCode;
    private final String message;
    private final int detailStatusCode;

    CustomExceptionInfo(int statusCode, String message, int detailStatusCode) {
        this.statusCode = statusCode;
        this.message = message;
        this.detailStatusCode = detailStatusCode;
    }
}
