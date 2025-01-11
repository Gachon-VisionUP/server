package GaVisionUp.server.entity.quest;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.TeamQuestGrade;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(nullable = false)
    private TeamQuestGrade questGrade; // ✅ 부여된 등급 (MAX, MEDIAN, MIN)

    @Column(nullable = false)
    private LocalDate recordedDate; // ✅ 실제 기록된 날짜

    public static TeamQuest create(User user, int month, int week, int day, LocalDate recordedDate, TeamQuestGrade questGrade) {
        return TeamQuest.builder()
                .user(user)
                .month(month)
                .week(week)
                .day(day) // ✅ day 값 저장
                .recordedDate(recordedDate)
                .questGrade(questGrade)
                .build();
    }
}
