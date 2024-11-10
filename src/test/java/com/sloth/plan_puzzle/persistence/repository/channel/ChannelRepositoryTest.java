package com.sloth.plan_puzzle.persistence.repository.channel;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.UNAUTHORIZED_ACCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.sloth.plan_puzzle.common.exception.CustomException;
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
import org.springframework.data.domain.Sort;
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
    private UserJpaEntity otherUserEntity;

    @BeforeEach
    void setUp() {
        userEntity = saveUserEntity("loginId");
        otherUserEntity = saveUserEntity("other");
        saveChannelEntity("study", otherUserEntity, "케이크 먹고 싶다.");
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

    @DisplayName("채널의 owner_id와 user의 id가 일치합니다.")
    @Test
    void getChannelByIdAndUserIdTest() {
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
    void getChannelByIdAndUserIdFailTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when,then
        assertThatThrownBy(() -> channelRepository.getChannelByIdAndUserId(channelEntity.getId(), 24L))
                .isInstanceOf(CustomException.class)
                .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
    }

    @DisplayName("채널의 owner_id와 유저 id가 일치하면 채널을 삭제할 수 있습니다.")
    @Test
    void deleteChannelByIdTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when
        channelRepository.deleteChannelById(channelEntity.getId(), userEntity.getId());
        //then
        assertThat(channelRepository.existsByIdAndUserId(channelEntity.getId(), userEntity.getId())).isFalse();
    }

    @DisplayName("채널의 owner_id와 유저 id가 일치하지 않으면 채널을 삭제할 수 없습니다.")
    @Test
    void deleteChannelByIdFailTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when,then
        assertThatThrownBy(() -> channelRepository.deleteChannelById(channelEntity.getId(), 24L))
                .isInstanceOf(CustomException.class)
                .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
    }

    @DisplayName("채널의 owner_id와 유저 id가 일치하지 않으면 예외가 발생합니다.")
    @Test
    void existsChannelByIdAndUserIdFailTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when,then
        assertThatThrownBy(
                () -> channelRepository.existsChannelByIdAndUserId(channelEntity.getId(), 24L))
                .isInstanceOf(CustomException.class)
                .hasMessage(UNAUTHORIZED_ACCESS.getMessage());
    }

    @DisplayName("채널의 owner_id와 유저 id가 일치하는지 확인할 수 있습니다.")
    @Test
    void existsChannelByIdAndUserIdTest() {
        //given
        ChannelJpaEntity channelEntity = saveChannelEntity("cake", userEntity, "케이크 먹고 싶다.");
        //when,then
        assertThatNoException().isThrownBy(
                () -> channelRepository.existsChannelByIdAndUserId(channelEntity.getId(), userEntity.getId()));
    }

    @DisplayName("title과 introdcution을 통해 채널을 검색할 수 있다.")
    @Test
    void getChannelsForSearchTest() {
        //given
        saveChannelEntity("cake", userEntity, "cafe pera");
        saveChannelEntity("cake", otherUserEntity, "cafe pera");
        saveChannelEntity("신라면", userEntity, "라멘보다는 라면!");
        saveChannelEntity("cake", userEntity, "cafe pera");
        saveChannelEntity("cake", userEntity, "cafe pera");
        saveChannelEntity("cake", userEntity, "cafe pera");
        saveChannelEntity("라멘 먹고 싶어!", otherUserEntity, "도쿄 여행");
        saveChannelEntity("이치란", userEntity, "이치란 라멘 왜 한국에 없어");
        saveChannelEntity("후쿠오카", userEntity, "후쿠오카 라멘");

        //when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

        //then
        assertThat(channelRepository.getChannelsForSearch("라멘", pageable))
                .hasSize(4)
                .extracting("nickname", "introduction")
                .containsExactly(
                        tuple("후쿠오카", "후쿠오카 라멘"),
                        tuple("이치란", "이치란 라멘 왜 한국에 없어"),
                        tuple("라멘 먹고 싶어!", "도쿄 여행"),
                        tuple("신라면", "라멘보다는 라면!")
                );

    }


    private ChannelJpaEntity saveChannelEntity(String nickname, UserJpaEntity userEntity, String introduction) {
        return channelRepository.save(ChannelJpaEntity.builder()
                .nickname(nickname)
                .introduction(introduction)
                .profileImgUrl("https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key")
                .backImgUrl("https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key")
                .userEntity(userEntity)
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