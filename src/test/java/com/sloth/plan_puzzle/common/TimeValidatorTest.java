package com.sloth.plan_puzzle.common;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_TIME_FORMAT;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.validator.TimeValidator;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TimeValidatorTest {
    @DisplayName("시간이 5분 단위가 맞습니다.")
    @Test
    void ValidateTimeFormatTest() {
        //given
        LocalDateTime time = LocalDateTime.parse("2024-11-11T11:10");

        //when, then
        assertThatNoException().isThrownBy(
                () -> TimeValidator.isValidateTimeFormat(time));


    }

    @DisplayName("시간이 5분 단위가 아니면 예외가 발생합니다.")
    @Test
    void ValidateTimeFormatFailTest() {
        //given
        LocalDateTime time = LocalDateTime.parse("2024-11-11T11:11");

        //when, then
        assertThatThrownBy(
                () -> TimeValidator.isValidateTimeFormat(time))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_TIME_FORMAT.getMessage());
    }
}