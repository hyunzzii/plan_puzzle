package com.sloth.plan_puzzle.persistence.repository.participation;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.PARTICIPATION_NOT_EXIST;
import static com.sloth.plan_puzzle.domain.recruitment.RecruitState.RECRUITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.participation.ParticipationJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.RecruitmentJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.recruitment.Region;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.recruitment.RecruitmentRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.util.Optional;
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
class ParticipationRepositoryTest {
    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private RecruitmentRepository recruitmentRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;

    private RecruitmentJpaEntity recruitmentEntity;
    private ChannelJpaEntity authorChannelEntity;
    private ChannelJpaEntity channelEntity;

    @BeforeEach
    void setUpd() {
        authorChannelEntity = saveChannelEntity("author", saveUserEntity("author"));
        recruitmentEntity = saveRecruitmentEntity(authorChannelEntity);
        channelEntity = saveChannelEntity("배고파", saveUserEntity("배고파"));
    }

    @DisplayName("participation이 존재하면 모집글을 탈퇴할 수 있습니다.")
    @Test
    void deleteParticipationByIdTest() {
        //given
        saveParticipationEntity(recruitmentEntity, channelEntity);

        //when
        participationRepository.deleteParticipationByRecruitmentIdAndChannelId(
                recruitmentEntity.getId(), channelEntity.getId());

        //then
        assertThat(participationRepository.findByRecruitmentIdAndChannelId(
                recruitmentEntity.getId(), channelEntity.getId()))
                .isEqualTo(Optional.empty());
    }

    @DisplayName("participation이 존재하지 않으면 예외가 발생합니다.")
    @Test
    void deleteParticipationByIdFailTest() {
        //given
        saveParticipationEntity(recruitmentEntity, channelEntity);

        //when

        //then
        assertThatThrownBy(() -> participationRepository.deleteParticipationByRecruitmentIdAndChannelId(
                recruitmentEntity.getId(), 24L))
                .isInstanceOf(CustomException.class)
                .hasMessage(PARTICIPATION_NOT_EXIST.getMessage());
    }

    @DisplayName("참여 관계이면 채널을 반환합니다.")
    @Test
    void getChannelByParticipationTest(){
        //given
        saveParticipationEntity(recruitmentEntity, channelEntity);

        //when
        ChannelJpaEntity foundChannelEntity = participationRepository.getChannelByParticipation(
                recruitmentEntity.getId(), channelEntity.getId());
        //then
        assertThat(foundChannelEntity).isEqualTo(channelEntity);

    }

    private ParticipationJpaEntity saveParticipationEntity(RecruitmentJpaEntity recruitmentEntity,
                                                           ChannelJpaEntity channelEntity) {
        return participationRepository.save(ParticipationJpaEntity
                .create(recruitmentEntity, channelEntity));
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