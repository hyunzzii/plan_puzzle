package com.sloth.plan_puzzle.persistence.repository.vote;

import static com.sloth.plan_puzzle.domain.recruitment.RecruitState.RECRUITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.Region;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.TimeSlotJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.VoteJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.recruitment.RecruitmentRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.time.LocalDateTime;
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
class TimeSlotRepositoryTest {
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    private VoteJpaEntity voteEntity;

    @BeforeEach
    void setUp() {
        RecruitmentJpaEntity recruitmentEntity1 = saveRecruitmentEntity(
                saveChannelEntity("hungry", saveUserEntity("abc")));
        RecruitmentJpaEntity recruitmentEntity2 = saveRecruitmentEntity(
                saveChannelEntity("shrimp", saveUserEntity("shrimp")));
        VoteJpaEntity otherVotEntity = saveVoteEntity(recruitmentEntity2);
        saveTimeSlotEntity(LocalDateTime.parse("2024-11-17T12:30"), LocalDateTime.parse("2024-11-17T14:30"),
                otherVotEntity);

        voteEntity = saveVoteEntity(recruitmentEntity1);
    }

    @DisplayName("투표 id로 time slot들을 조회할 수 있습니다.")
    @Test
    void findListByVoteIdTest() {
        //given
        saveTimeSlotEntity(LocalDateTime.parse("2024-11-18T12:30"), LocalDateTime.parse("2024-11-18T14:30"),
                voteEntity);
        saveTimeSlotEntity(LocalDateTime.parse("2024-11-20T13:00"), LocalDateTime.parse("2024-11-20T15:00"),
                voteEntity);
        saveTimeSlotEntity(LocalDateTime.parse("2024-11-18T11:30"), LocalDateTime.parse("2024-11-18T13:30"),
                voteEntity);
        //when
        //then
        assertThat(timeSlotRepository.findListByVoteId(voteEntity.getId()))
                .hasSize(3)
                .extracting("startDateTime", "endDateTime")
                .containsExactly(
                        tuple(LocalDateTime.parse("2024-11-18T11:30"), LocalDateTime.parse("2024-11-18T13:30")),
                        tuple(LocalDateTime.parse("2024-11-18T12:30"), LocalDateTime.parse("2024-11-18T14:30")),
                        tuple(LocalDateTime.parse("2024-11-20T13:00"), LocalDateTime.parse("2024-11-20T15:00"))
                );

    }

    private TimeSlotJpaEntity saveTimeSlotEntity(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                 VoteJpaEntity voteEntity) {
        return timeSlotRepository.save(
                TimeSlotJpaEntity.create(startDateTime, endDateTime, voteEntity));
    }

    private VoteJpaEntity saveVoteEntity(RecruitmentJpaEntity recruitmentEntity) {
        return voteRepository.save(
                VoteJpaEntity.create(LocalDateTime.parse("2024-11-10T12:00"), recruitmentEntity));
    }

    private RecruitmentJpaEntity saveRecruitmentEntity(ChannelJpaEntity authorEntity) {
        return recruitmentRepository.save(RecruitmentJpaEntity.create(
                "title", "content", 5, RECRUITING,
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key",
                Region.builder().province("경기도").city("고양시").build(), authorEntity));
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