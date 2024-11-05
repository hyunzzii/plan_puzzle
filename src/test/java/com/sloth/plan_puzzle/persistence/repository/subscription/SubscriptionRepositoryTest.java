package com.sloth.plan_puzzle.persistence.repository.subscription;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.subscription.SubscriptionJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.channel.NoticeRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
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
class SubscriptionRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private UserJpaEntity subscriberUser;
    private UserJpaEntity subscribedUser;

    private ChannelJpaEntity subscriberChannel;
    private ChannelJpaEntity subscribedChannel;

    @BeforeEach
    void setUp() {
        subscriberUser = saveUserEntity("subscriber_user");
        subscribedUser = saveUserEntity("subscribed_user");

        subscriberChannel = saveChannelEntity(
                "subscriber_channel", subscriberUser, "subscriber 입니다.");
        subscribedChannel = saveChannelEntity(
                "subscribed_channel", subscribedUser, "subscribed 입니다.");
        SubscriptionJpaEntity subscriptionEntity = SubscriptionJpaEntity.create(
                saveChannelEntity("other", subscriberUser, "기본데이터")
                , subscribedChannel);
        subscriptionRepository.save(subscriptionEntity);
    }

    @DisplayName("해당하는 subscriber와 subscribed간의 관계가 없으면 구독을 취소할 수 없습니다.")
    @Test
    void subscribeFailWhenUserIsNotOwnerTest() {
        //given
        //when
        //then
        assertThatThrownBy(() -> subscriptionRepository.deleteSubscriptionBySubscribe(
                subscriberChannel.getId(), subscribedChannel.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(SUBSCRIPTION_NOT_EXIST.getMessage());

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