package com.sloth.plan_puzzle.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomExceptionInfo customExceptionInfo;

    public CustomException(CustomExceptionInfo customExceptionInfo) {
        super(customExceptionInfo.getMessage());
        this.customExceptionInfo = customExceptionInfo;
    }
}
