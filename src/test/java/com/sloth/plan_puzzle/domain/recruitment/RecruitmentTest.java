package com.sloth.plan_puzzle.domain.recruitment;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_RECRUIT_CAPACITY;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.persistence.entity.recruitment.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecruitmentTest {

    @DisplayName("현재 모집된 인원보다 업데이트하려는 모집정원이 작으면 업데이트할 수 없습니다.")
    @Test
    void validateRecruitCapacityForUpdateFailTest() {
        //given
        Recruitment recruitment = createRecruitment(5);
        Recruitment recruitmentForUpdate = createRecruitment(4);

        //when, then
        assertThatThrownBy(
                () -> recruitmentForUpdate.validateRecruitCapacityForUpdate(recruitment.recruitCapacity()))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_RECRUIT_CAPACITY.getMessage());
    }

    @DisplayName("현재 모집된 인원보다 업데이트하려는 모집정원이 크면 업데이트 할 수 있습니다.")
    @Test
    void validateRecruitCapacityForUpdateTest() {
        //given
        Recruitment recruitment = createRecruitment(5);
        Recruitment recruitmentForUpdate = createRecruitment(6);

        //when, then
        assertThatNoException().isThrownBy(
                () -> recruitmentForUpdate.validateRecruitCapacityForUpdate(recruitment.recruitCapacity()));
    }

    private Recruitment createRecruitment(int recruitCapacity) {
        return Recruitment.builder()
                .title("제목")
                .content("내용")
                .recruitCapacity(recruitCapacity)
                .recruitCount(5)
                .recruitState(RecruitState.RECRUITING)
                .imgUrl("https://planpuzzle.s3.us-west-1.amazonaws.com/sample-key")
                .region(Region.builder().province("경기도").city("고양시").build())
                .authorId(1L)
                .build();
    }
}