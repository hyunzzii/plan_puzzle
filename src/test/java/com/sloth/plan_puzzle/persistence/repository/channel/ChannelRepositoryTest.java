package com.sloth.plan_puzzle.persistence.repository.channel;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class ChannelRepositoryTest {
    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    private UserJpaEntity userEntity;

    @BeforeEach
    void setUp() {
        UserJpaEntity otherUserEntity = saveUserEntity("other");
        saveChannelEntity("study", otherUserEntity, "케이크 먹고 싶다.");
        userEntity = saveUserEntity("loginId");
    }


    @DisplayName("user가 가지고 있는 채널을 조회할 수 있습니다.")
    @Test
    void findByUserIdTest() {
        //given
        saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        saveChannelEntity("strawberry", userEntity, "케이크 먹고 싶다.");
        saveChannelEntity("coconut", userEntity, "케이크 먹고 싶다.");
        //when
        List<ChannelJpaEntity> foundChannelEntity = channelRepository.findByUserId(userEntity.getId());
        //then
        assertThat(foundChannelEntity)
                .hasSize(3)
                .extracting("nickname", "user")
                .containsExactlyInAnyOrder(
                        tuple("cake", userEntity),
                        tuple("strawberry", userEntity),
                        tuple("coconut", userEntity)
                );

    }

    @DisplayName("채널의 nickname과 intrdouction으로 검색할 수 있습니다.")
    @Test
    void channelSearchTest() {
        //given
        saveChannelEntity("타르트", userEntity, "카페 페라는 사랑!");
        saveChannelEntity("딸기우유", userEntity, "딸기 치즈 타르트 최고");
        saveChannelEntity("망고+코코넛", userEntity, "망고 코코넛 스무디 먹고싶다...");
        //when
        Pageable pageable = PageRequest.of(0, 10);
        List<ChannelJpaEntity> foundChannelEntity = channelRepository.getChannelsForSearch("타르트",pageable)
                        .stream().toList();
        //then
        assertThat(foundChannelEntity)
                .hasSize(2)
                .extracting("nickname", "user","introduction")
                .containsExactly(
                        tuple("타르트", userEntity,"카페 페라는 사랑!"),
                        tuple("딸기우유", userEntity,"딸기 치즈 타르트 최고")
                );

    }

    @DisplayName("채널의 owner_id와 user의 id가 일치합니다.")
    @Test
    void findByIdAndUserIdTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when
        ChannelJpaEntity foundChannelEntity = channelRepository.getChannelByIdAndUserId(channelEntity.getId(),
                userEntity.getId());
        //then
        assertThat(foundChannelEntity).isEqualTo(channelEntity);
    }

    @DisplayName("채널의 owner_id와 user의 id가 일치하지 않으면 예외가 발생합니다.")
    @Test
    void findByIdAndUserIdFailTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when,then
        assertThatThrownBy(() -> channelRepository.getChannelByIdAndUserId(channelEntity.getId(), 24L))
                .isInstanceOf(CustomException.class)
                .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
    }

    @DisplayName("채널의 owner_id와 유저 id가 일치하면 채널을 삭제할 수 있습니다.")
    @Test
    void deleteByIdAndUserIdTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when
        channelRepository.deleteChannelById(channelEntity.getId(),userEntity.getId());
        //then
        assertThat(channelRepository.existsByIdAndUserId(channelEntity.getId(),userEntity.getId())).isFalse();
    }

    @DisplayName("채널의 owner_id와 유저 id가 일치하지 않으면 채널을 삭제할 수 없습니다.")
    @Test
    void deleteByIdAndUserIdFailTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when,then
        assertThatThrownBy(() -> channelRepository.deleteChannelById(channelEntity.getId(), 24L))
                .isInstanceOf(CustomException.class)
                .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
    }


    private ChannelJpaEntity saveChannelEntity(String nickname, UserJpaEntity userEntity, String introduction) {
        return channelRepository.save(ChannelJpaEntity.builder()
                .nickname(nickname)
                .introduction(introduction)
                .profileImgUrl("https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key")
                .backImgUrl("https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key")
                .user(userEntity)
                .build());
    }

    private UserJpaEntity saveUserEntity(String loginId) {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(UserJpaEntity.builder()
                .loginId(loginId)
                .loginPw(passwordEncoder.encode("password"))
                .name("test")
                .email("test@ajou.ac.kr")
                .gender(Gender.FEMALE)
                .ageGroup(AgeGroup.TWENTIES)
                .role(UserRole.ROLE_USER)
                .build());
    }
}