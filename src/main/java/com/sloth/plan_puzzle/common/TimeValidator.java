package com.sloth.plan_puzzle.common;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_TIME_FORMAT;

import com.sloth.plan_puzzle.common.exception.CustomException;
import java.time.LocalDateTime;

public class TimeValidator {

    public static void isValidateTimeFormat(final LocalDateTime dateTime) {
        if (dateTime.getMinute() % 5 == 0 && dateTime.getSecond() == 0) {
            return;
        }
        throw new CustomException(INVALID_TIME_FORMAT);
    }
}
