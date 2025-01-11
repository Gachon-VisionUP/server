package GaVisionUp.server.entity.quest;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.TeamQuestGrade;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "team_quest")
public class TeamQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // ✅ 퀘스트 수행자

    @Column(name = "month_value", nullable = false)
    private int month; // ✅ 퀘스트 달성 월 (1~12)

    @Column(name = "week_value", nullable = false)
    private int week; // ✅ 퀘스트 달성 주차 (1~5)

    @Column(name = "day_value", nullable = false)
    private int day; // ✅ 퀘스트 달성 일 (1~31)

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // ✅ 요일 필드 추가 (월요일~일요일)

    @Enumerated(EnumType.STRING)
    @Column(name = "job_grade", nullable = true)
    private TeamQuestGrade jobGrade; // ✅ 직무별 퀘스트 평가 등급 (nullable)

    @Enumerated(EnumType.STRING)
    @Column(name = "leader_grade", nullable = true)
    private TeamQuestGrade leaderGrade; // ✅ 리더 부여 퀘스트 평가 등급 (nullable)

    @Column(nullable = false)
    private LocalDate recordedDate; // ✅ 실제 기록된 날짜

    public static TeamQuest create(User user, int month, int week, int day, DayOfWeek dayOfWeek, LocalDate recordedDate, TeamQuestGrade jobGrade, TeamQuestGrade leaderGrade) {
        return TeamQuest.builder()
                .user(user)
                .month(month)
                .week(week)
                .day(day)
                .dayOfWeek(dayOfWeek) // ✅ 요일 저장
                .recordedDate(recordedDate)
                .jobGrade(jobGrade) // ✅ 직무별 퀘스트 평가 등급 저장
                .leaderGrade(leaderGrade) // ✅ 리더 부여 퀘스트 평가 등급 저장
                .build();
    }
}
