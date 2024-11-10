package com.sloth.plan_puzzle.persistence.repository.recruitment;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.UNAUTHORIZED_ACCESS;
import static com.sloth.plan_puzzle.domain.recruitment.RecruitState.RECRUITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.Region;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class RecruitmentRepositoryTest {
    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;


    private ChannelJpaEntity channelEntity;
    private ChannelJpaEntity otherChannelEntity;

    @BeforeEach
    void setUp() {
        channelEntity = saveChannelEntity("test", saveUserEntity("test"));
        otherChannelEntity = saveChannelEntity("other", saveUserEntity("other"));
    }

    @DisplayName("제목과 소개글, 지역으로 검색할 수 있습니다.")
    @Test
    void getRecruitmentListForSearchTest() {
        //given
        saveRecruitmentEntity(channelEntity, "케이크", "딸기 케이크 최고", "고양시");
        saveRecruitmentEntity(otherChannelEntity, "라면", "이치란 라멘 먹고싶다.", "도쿄");
        saveRecruitmentEntity(channelEntity,"일본가고 싶어.","일본가서 라멘 먹기!", "도쿄");
        saveRecruitmentEntity(channelEntity,"일본가고 싶어.","일본가서 라멘 먹기!", "오사카");

        //when, then
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        assertThat(recruitmentRepository.getRecruitmentListForSearch("도쿄",pageable))
                .hasSize(2)
                .extracting("title","content","region.city")
                .containsExactly(
                        tuple("일본가고 싶어.","일본가서 라멘 먹기!","도쿄"),
                        tuple("라면", "이치란 라멘 먹고싶다.","도쿄")
                );
    }

    @DisplayName("모집글 작성자 채널이 user의 채널임을 확인합니다.")
    @Test
    void getRecruitmentByIdAndAuthorIdTest(){
        //given
        RecruitmentJpaEntity recruitmentEntity = saveRecruitmentEntity(
                channelEntity, "케이크", "딸기 케이크 최고", "고양시");

        //when
        //then
        assertThat(recruitmentRepository.getRecruitmentByIdAndAuthorId(
                recruitmentEntity.getId(), channelEntity.getId()
        )).isEqualTo(recruitmentEntity);

    }

    @DisplayName("모집글 작성자 채널이 user의 채널이 아니면 예외가 발생합니다.")
    @Test
    void getRecruitmentByIdAndAuthorIdFailTest(){
        //given
        RecruitmentJpaEntity recruitmentEntity = saveRecruitmentEntity(
                channelEntity, "케이크", "딸기 케이크 최고", "고양시");

        //when
        //then
        assertThatThrownBy(() -> recruitmentRepository.getRecruitmentByIdAndAuthorId(
                recruitmentEntity.getId(), otherChannelEntity.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
    }

    private RecruitmentJpaEntity saveRecruitmentEntity(ChannelJpaEntity authorEntity, String title, String content,
                                                       String city) {
        return recruitmentRepository.save(RecruitmentJpaEntity.create(
                title, content, 5, RECRUITING,
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key",
                Region.builder().province("경기도").city(city).build(), authorEntity));
    }

    private ChannelJpaEntity saveChannelEntity(String nickname, UserJpaEntity userEntity) {
        return channelRepository.save(ChannelJpaEntity.create(nickname, "소개",
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key",
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key",
                userEntity));
    }

    private UserJpaEntity saveUserEntity(String loginId) {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(
                UserJpaEntity.create(loginId, passwordEncoder.encode("password"), "이름", "email",
                        Gender.FEMALE, AgeGroup.TWENTIES, UserRole.ROLE_USER));
    }

}