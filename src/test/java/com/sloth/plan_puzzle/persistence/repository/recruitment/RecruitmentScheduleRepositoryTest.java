package com.sloth.plan_puzzle.persistence.repository.recruitment;

import static com.sloth.plan_puzzle.domain.recruitment.RecruitState.RECRUITING;
import static org.assertj.core.api.Assertions.*;

import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentScheduleJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.Region;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.time.LocalDateTime;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class RecruitmentScheduleRepositoryTest {
    @Autowired
    private RecruitmentScheduleRepository recruitmentScheduleRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    private RecruitmentJpaEntity recruitmentEntity;

    @BeforeEach
    void setUp() {
        ChannelJpaEntity channelEntity = saveChannelEntity();
        recruitmentEntity = saveRecruitmentEntity(channelEntity);
    }

    @DisplayName("모집글 아이디로 모집글 스케줄을 조회할 수 있으며, 스케줄의 시작 시간이 가장 빠른 순입니다.")
    @Test
    void findByRecruitmentIdTest() {
        //given
        //when
        saveRecruitmentScheduleEntity(recruitmentEntity, "2차만남", LocalDateTime.parse("2024-11-13T12:00"));
        saveRecruitmentScheduleEntity(recruitmentEntity, "1차만남", LocalDateTime.parse("2024-11-11T16:30"));
        saveRecruitmentScheduleEntity(recruitmentEntity, "3차만남", LocalDateTime.parse("2024-11-18T20:00"));
        //then
        assertThat(recruitmentScheduleRepository.findByRecruitmentId(recruitmentEntity.getId()))
                .hasSize(3)
                .extracting("title", "startDateTime")
                .containsExactly(
                        tuple("1차만남", LocalDateTime.parse("2024-11-11T16:30")),
                        tuple("2차만남", LocalDateTime.parse("2024-11-13T12:00")),
                        tuple("3차만남", LocalDateTime.parse("2024-11-18T20:00"))
                );

    }


    private RecruitmentScheduleJpaEntity saveRecruitmentScheduleEntity(RecruitmentJpaEntity recruitmentEntity,
                                                                       String title, LocalDateTime startDateTime) {
        return recruitmentScheduleRepository.save(RecruitmentScheduleJpaEntity.create(
                startDateTime, startDateTime.minusHours(2),
                title, null, recruitmentEntity));
    }

    private RecruitmentJpaEntity saveRecruitmentEntity(ChannelJpaEntity authorEntity) {
        return recruitmentRepository.save(RecruitmentJpaEntity.create(
                "제목", "내용", 5, RECRUITING,
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key",
                Region.builder().province("경기도").city("고양시").build(), authorEntity));
    }

    private ChannelJpaEntity saveChannelEntity() {
        return channelRepository.save(ChannelJpaEntity.create("배고파", "소개",
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key",
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key",
                saveUserEntity()));
    }

    private UserJpaEntity saveUserEntity() {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(
                UserJpaEntity.create("loginId", passwordEncoder.encode("password"), "이름", "email",
                        Gender.FEMALE, AgeGroup.TWENTIES, UserRole.ROLE_USER));
    }
}