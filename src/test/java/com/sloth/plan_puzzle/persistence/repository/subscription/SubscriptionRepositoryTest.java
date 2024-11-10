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
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private ChannelJpaEntity subscriberChannel;
    private ChannelJpaEntity subscribedChannel;

    @BeforeEach
    void setUp() {
        subscriberChannel = saveChannelEntity(
                "subscriber_channel", saveUserEntity("subscriber_user"), "subscriber 입니다.");
        subscribedChannel = saveChannelEntity(
                "subscribed_channel", saveUserEntity("subscribed_user"), "subscribed 입니다.");
    }

    @DisplayName("해당하는 subscriber와 subscribed간의 구독을 취소할 수 있습니다.")
    @Test
    void subscribeWhenUserIsOwnerTest() {
        //given
        //when
        saveSubscriptionEntity(subscriberChannel, subscribedChannel);
        //then
        assertThatNoException().isThrownBy(() -> subscriptionRepository.deleteSubscriptionBySubscribe(
                subscriberChannel.getId(), subscribedChannel.getId()));

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

    @DisplayName("채널 id를 통해 구독중인 채널을 모두 불러옵니다.")
    @Test
    void findBySubscriberIdTest() {
        //given
        ChannelJpaEntity subscribedChannel2 = saveChannelEntity(
                "subscribed_channel2", saveUserEntity("subscriber_user2"), "subscribed2 입니다.");

        ChannelJpaEntity subscribedChannel3 = saveChannelEntity(
                "subscribed_channel3", saveUserEntity("subscriber_user3"), "subscribed3 입니다.");
        saveSubscriptionEntity(subscribedChannel2, subscribedChannel3);

        //when
        saveSubscriptionEntity(subscriberChannel, subscribedChannel);
        saveSubscriptionEntity(subscriberChannel, subscribedChannel2);
        saveSubscriptionEntity(subscriberChannel, subscribedChannel3);

        //then
        assertThat(subscriptionRepository.findBySubscriberId(subscriberChannel.getId()))
                .hasSize(3)
                .extracting("subscribed")
                .containsExactlyInAnyOrder(subscribedChannel, subscribedChannel2, subscribedChannel3);

    }

    private SubscriptionJpaEntity saveSubscriptionEntity(ChannelJpaEntity subscriberChannel,
                                                         ChannelJpaEntity subscribedChannel) {
        return subscriptionRepository.save(SubscriptionJpaEntity.create(subscriberChannel, subscribedChannel));
    }

    private ChannelJpaEntity saveChannelEntity(String nickname, UserJpaEntity userEntity, String introduction) {
        return channelRepository.save(ChannelJpaEntity.create(nickname, introduction,
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key",
                "https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key", userEntity));
    }

    private UserJpaEntity saveUserEntity(String loginId) {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(UserJpaEntity.create(loginId, passwordEncoder.encode("password"),
                "홍길동", "test@ajou.ac.kr", Gender.FEMALE, AgeGroup.TWENTIES, UserRole.ROLE_USER));
    }
}