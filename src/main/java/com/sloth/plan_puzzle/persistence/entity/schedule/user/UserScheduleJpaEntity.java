package com.sloth.plan_puzzle.persistence.entity.schedule.user;

import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CANDIDATE;
import static com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState.CONFIRMED;

import com.sloth.plan_puzzle.domain.schedule.user.UserSchedule;
import com.sloth.plan_puzzle.domain.schedule.user.UserScheduleState;
import com.sloth.plan_puzzle.persistence.entity.user.UserJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user-schedule")
public class UserScheduleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shcedule_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String title;

    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserScheduleState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

    @Builder
    private UserScheduleJpaEntity(final LocalDateTime startDateTime, final LocalDateTime endDateTime,
                                  final String title, final String content, final UserScheduleState state,
                                  final UserJpaEntity user) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.title = title;
        this.content = content;
        this.state = state;
        this.user = user;
    }

    public static UserScheduleJpaEntity create(final LocalDateTime startDateTime, final LocalDateTime endDateTime,
                                               final String title, final String content, final UserScheduleState state,
                                               final UserJpaEntity userEntity) {
        return UserScheduleJpaEntity.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .title(title)
                .content(content)
                .state(state)
                .user(userEntity)
                .build();
    }

    public void update(final UserSchedule userSchedule) {
        startDateTime = userSchedule.startDateTime();
        endDateTime = userSchedule.endDateTime();
        title = userSchedule.title();
        content = userSchedule.content();
    }

    public void toggleStatus() {
        state = state == CONFIRMED ? CANDIDATE : CONFIRMED;
    }
}
