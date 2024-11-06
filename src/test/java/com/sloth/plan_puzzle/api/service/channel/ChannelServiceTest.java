package com.sloth.plan_puzzle.api.service.channel;

import static org.assertj.core.api.Assertions.*;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import com.sloth.plan_puzzle.common.security.jwt.RedisUtil;
import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.channel.ChannelRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@MockBean(RedisUtil.class)
@Transactional
public class ChannelServiceTest {
    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("채널의 닉네임이 중복이 아닙니다.")
    @Test
    void isDuplicateNicknameTest(){
        //given
        saveChannelEntity("coconut",saveUserEntity(),"coconut+mango");

        //when, then
        assertThatNoException().isThrownBy(()->channelService.isDuplicateNickname("strawberry"));

    }

    @DisplayName("채널의 닉네임이 중복이면 예외가 발생합니다..")
    @Test
    void isDuplicateNicknameFailTest(){
        //given
        saveChannelEntity("coconut",saveUserEntity(),"coconut+mango");

        //when, then
        assertThatThrownBy(() -> channelService.isDuplicateNickname("coconut"))
                .isInstanceOf(CustomException.class)
                .hasMessage(CustomExceptionInfo.DUPLICATE_NICKNAME.getMessage());
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

    private UserJpaEntity saveUserEntity() {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(UserJpaEntity.builder()
                .loginId("loginId")
                .loginPw(passwordEncoder.encode("password"))
                .name("test")
                .email("test@ajou.ac.kr")
                .gender(Gender.FEMALE)
                .ageGroup(AgeGroup.TWENTIES)
                .role(UserRole.ROLE_USER)
                .build());
    }
}
