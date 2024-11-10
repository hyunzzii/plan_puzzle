package com.sloth.plan_puzzle.persistence.entity.user;

import com.sloth.plan_puzzle.domain.user.AgeGroup;
import com.sloth.plan_puzzle.domain.user.Gender;
import com.sloth.plan_puzzle.domain.user.UserRole;
import com.sloth.plan_puzzle.persistence.entity.BaseTimeEntity;
import com.sloth.plan_puzzle.persistence.entity.channel.ChannelJpaEntity;
import com.sloth.plan_puzzle.persistence.entity.schedule.UserScheduleJpaEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"user\"")
public class UserJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String loginPw;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<UserScheduleJpaEntity> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<ChannelJpaEntity> channels = new ArrayList<>();

    @Builder
    private UserJpaEntity(
            final String loginId, final String loginPw, final String name, final String email, final Gender gender,
            final AgeGroup ageGroup, final UserRole role
    ) {
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.role = role;
    }

    public static UserJpaEntity create(final String loginId, final String loginPw, final String name,
                                       final String email, final Gender gender, final AgeGroup ageGroup,
                                       final UserRole role) {
        return UserJpaEntity.builder()
                .loginId(loginId)
                .loginPw(loginPw)
                .name(name)
                .email(email)
                .ageGroup(ageGroup)
                .gender(gender)
                .role(role)
                .build();
    }

//    public void addChannel(final ChannelJpaEntity channelEntity){
//        this.channels.add(channelEntity);
//    }
//
//    public void addSchedule(final UserScheduleJpaEntity scheduleEntity){
//        this.schedules.add(scheduleEntity);
//    }
}
