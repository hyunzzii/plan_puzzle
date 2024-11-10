package com.sloth.plan_puzzle.persistence.repository.channel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.channel.NoticeJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.subscription.SubscriptionJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import com.sloth.plan_puzzle.persistence.repository.subscription.SubscriptionRepository;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
class NoticeRepositoryTest {
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;

    private ChannelJpaEntity channelEntity;

    @BeforeEach
    void setUp() throws Exception {
        channelEntity = saveChannelEntity("딸기타르트");
        ChannelJpaEntity otherChannelEntity = saveChannelEntity("배고파");
        setCreatedDate(saveNoticeEntity("11월 행사 취소", "11월의 행사가 취소되었습니다.", channelEntity),
                LocalDateTime.parse("2024-10-28T00:00"));
        setCreatedDate(saveNoticeEntity("홍대 방탈출 행사 공지합니다.", "일정에 참고해주세요.", channelEntity),
                LocalDate.now().minusDays(5).atStartOfDay());
        setCreatedDate(saveNoticeEntity("클라이밍 규칙", "규칙 말해요.", channelEntity),
                LocalDate.now().minusDays(3).atStartOfDay());
        setCreatedDate(saveNoticeEntity("구독하지 마세요.", "규칙 말해요.", otherChannelEntity),
                LocalDateTime.parse("2024-11-05T00:00"));
    }

    @DisplayName("channel의 id에 해당하는 notice들을 찾을 수 있습니다.")
    @Test
    void findByChannelIdTest() {
        //given
        //when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        List<NoticeJpaEntity> foundNotices =
                noticeRepository.findByChannelId(channelEntity.getId(), pageable)
                        .stream().toList();

        //then
        assertThat(foundNotices).hasSize(3)
                .extracting("title", "channel")
                .containsExactly(
                        tuple("클라이밍 규칙", channelEntity),
                        tuple("홍대 방탈출 행사 공지합니다.", channelEntity),
                        tuple("11월 행사 취소", channelEntity)
                );
    }

    @DisplayName("구독하는 채널들의 최근 공지들을 조회합니다.")
    @Test
    void findRecentNoticesBySubscriberId() {
        //given
        ChannelJpaEntity myChannelEntity = saveChannelEntity("myChannel");
        subscriptionRepository.save(SubscriptionJpaEntity.create(myChannelEntity, channelEntity));
        //when
        List<NoticeJpaEntity> foundNotices =
                noticeRepository.findRecentNoticesBySubscriberId(myChannelEntity.getId(),
                                LocalDate.now().minusWeeks(1).atStartOfDay())
                        .stream().toList();

        //then
        assertThat(foundNotices).hasSize(2)
                .extracting("title", "createdDate", "channel")
                .containsExactly(
                        tuple("클라이밍 규칙", LocalDate.now().minusDays(3).atStartOfDay(), channelEntity),
                        tuple("홍대 방탈출 행사 공지합니다.", LocalDate.now().minusDays(5).atStartOfDay(), channelEntity)
                );
    }

    private void setCreatedDate(Object entity, LocalDateTime date) throws Exception {
        Field field = entity.getClass().getSuperclass().getDeclaredField("createdDate");
        field.setAccessible(true);
        field.set(entity, date);
    }

    private NoticeJpaEntity saveNoticeEntity(String title, String content, ChannelJpaEntity channelEntity) {
        return noticeRepository.save(NoticeJpaEntity.create(title,content,channelEntity));
    }


    private ChannelJpaEntity saveChannelEntity(String nickname) {
        return channelRepository.save(ChannelJpaEntity.builder()
                .nickname(nickname)
                .introduction("배고파요")
                .profileImgUrl("https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key")
                .backImgUrl("https://planpuzzle-bucket.s3.us-west-1.amazonaws.com/sample-key")
                .userEntity(saveUserEntity())
                .build());
    }

    private UserJpaEntity saveUserEntity() {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(
                UserJpaEntity.create(
                        "loginId", passwordEncoder.encode("password"), "test",
                        "test@ajou.ac.kr", Gender.FEMALE, AgeGroup.TWENTIES, UserRole.ROLE_USER
                ));
    }

}