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
import com.sloth.plan_puzzle.persistence.entity.vote.ChannelVoteJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.TimeSlotJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.vote.VoteJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.recruitment.RecruitmentRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
class ChannelVoteRepositoryTest {
    @Autowired
    private ChannelVoteRepository channelVoteRepository;

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


    private List<ChannelJpaEntity> channelEntityList = new ArrayList<>();
    private List<TimeSlotJpaEntity> timeSlotEntityList = new ArrayList<>();
    private VoteJpaEntity voteEntity;


    @BeforeEach
    void setUp() {
        channelEntityList.add(saveChannelEntity("channel1", saveUserEntity("@ldfj!")));
        channelEntityList.add(saveChannelEntity("channel2", saveUserEntity("@ldfj!")));
        channelEntityList.add(saveChannelEntity("channel3", saveUserEntity("@ldfj!")));
        channelEntityList.add(saveChannelEntity("channel4", saveUserEntity("@ldfj!")));
        RecruitmentJpaEntity recruitmentEntity1 = saveRecruitmentEntity(
                saveChannelEntity("hungry", saveUserEntity("abc")));
        voteEntity = saveVoteEntity(recruitmentEntity1);
        timeSlotEntityList.add(saveTimeSlotEntity(LocalDateTime.parse("2024-11-24T12:30"),
                LocalDateTime.parse("2024-11-24T14:30"), voteEntity));
        timeSlotEntityList.add(saveTimeSlotEntity(LocalDateTime.parse("2024-11-23T12:30"),
                LocalDateTime.parse("2024-11-23T14:30"), voteEntity));
        timeSlotEntityList.add(saveTimeSlotEntity(LocalDateTime.parse("2024-11-22T13:00"),
                LocalDateTime.parse("2024-11-22T15:00"), voteEntity));
        timeSlotEntityList.add(saveTimeSlotEntity(LocalDateTime.parse("2024-12-31T00:15"),
                LocalDateTime.parse("2024-12-31T02:15"), voteEntity));

        saveChannelVoteEntity(timeSlotEntityList.get(0), channelEntityList.get(1));
        saveChannelVoteEntity(timeSlotEntityList.get(1), channelEntityList.get(3));
        saveChannelVoteEntity(timeSlotEntityList.get(1), channelEntityList.get(3));
        saveChannelVoteEntity(timeSlotEntityList.get(2), channelEntityList.get(0));
        saveChannelVoteEntity(timeSlotEntityList.get(3), channelEntityList.get(0));
        saveChannelVoteEntity(timeSlotEntityList.get(2), channelEntityList.get(3));
        saveChannelVoteEntity(timeSlotEntityList.get(2), channelEntityList.get(1));
        saveChannelVoteEntity(timeSlotEntityList.get(1), channelEntityList.get(1));


    }

    @DisplayName("time slot Id로 누가 투표했는지 알 수 있습니다.")
    @Test
    void findListByTimeSlotIdTest() {
        //given
        //when
        //then
        assertThat(channelVoteRepository.findListByTimeSlotId(timeSlotEntityList.get(2).getId()))
                .hasSize(3)
                .extracting("channel")
                .containsExactlyInAnyOrder(channelEntityList.get(1),
                        channelEntityList.get(3), channelEntityList.get(0));
    }

    @DisplayName("time slot Id를 몰라도 voite와 channel로 진행한 투표를 조회할 수 있습니다.")
    @Test
    void findListByVoteIdAndChannelIdTest() {
        //given
        //when
        ChannelVoteJpaEntity channelVoteEntity1 = saveChannelVoteEntity(timeSlotEntityList.get(0), channelEntityList.get(2));
        ChannelVoteJpaEntity channelVoteEntity2 = saveChannelVoteEntity(timeSlotEntityList.get(0), channelEntityList.get(2));

        //then
        assertThat(channelVoteRepository.findListByVoteIdAndChannelId(voteEntity.getId(),
                channelEntityList.get(2).getId()))
                .hasSize(2)
                .containsExactlyInAnyOrder(channelVoteEntity1,channelVoteEntity2);
    }

    private ChannelVoteJpaEntity saveChannelVoteEntity(TimeSlotJpaEntity timeSlotEntity,
                                                       ChannelJpaEntity channelEntity) {
        return channelVoteRepository.save(ChannelVoteJpaEntity
                .create(timeSlotEntity, channelEntity));
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