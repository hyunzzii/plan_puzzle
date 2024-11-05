package com.sloth.plan_puzzle.common;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_IMG_URL;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sloth.plan_puzzle.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlValidatorTest {
    @DisplayName("img-url이 올바른지 검증합니다.")
    @Test
    void validateChannelImgUrlTest() {
        //given
        String imgUrl = "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key";
        //when, then
        assertThatNoException().isThrownBy(()->UrlValidator.isValidateUrl(imgUrl));
    }

    @DisplayName("채널의 img-url이 올바르지 않으면 예외가 발생합니다.")
    @Test
    void validateChannelImgUrlFailTest() {
        //givenÒ
        String imgUrl = "https://planpuzzle.s3.us-west-1.amazonaws.com/sample-key";
        //when, then
        assertThatThrownBy(()->UrlValidator.isValidateUrl(imgUrl))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_IMG_URL.getMessage());
    }

}