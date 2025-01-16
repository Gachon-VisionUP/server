package GaVisionUp.server.entity.quest.leader;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Cycle;
import GaVisionUp.server.entity.enums.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "leader_quest")
public class LeaderQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // ✅ 퀘스트 수행자 (리더)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department; // ✅ 소속 (ex: "음성 1센터")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cycle cycle; // ✅ 주기 (ex: "월간", "주간")

    @Column(name = "month_value", nullable = false)
    private int month; // ✅ 해당 월 (1~12)

    @Column(nullable = true)
    private Integer week; // ✅ 주단위 퀘스트일 경우 주차 (1~52)

    @Column(nullable = false)
    private String questName; // ✅ 리더 부여 퀘스트명 (ex: "월특근", "업무개선")

    @Column(nullable = false)
    private String achievementType; // ✅ 달성 내용 (Max인지 Median인지, 기본적으로 빈 값)

    @Column(nullable = false)
    private int grantedExp; // ✅ 최종 부여된 경험치 (Max/Mid 기준 적용, 기본적으로 0)

    @Column(nullable = true)
    private String note; // ✅ 비고 (추가 설명, 기본적으로 빈 값)

    @Column(nullable = false)
    private LocalDate assignedDate; // ✅ 퀘스트 달성 날짜

    // ✅ 퀘스트 조건 매핑 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id", referencedColumnName = "id", nullable = false)
    private LeaderQuestCondition condition;

    public static LeaderQuest create(User user, Cycle cycle, String questName, int month, Integer week,
                                     String achievementType, int grantedExp, String note,
                                     LocalDate assignedDate, LeaderQuestCondition condition) {
        return LeaderQuest.builder()
                .user(user)
                .department(user.getDepartment())
                .cycle(cycle) // ✅ 주기 설정
                .month(month)
                .week(week) // ✅ 주단위 퀘스트일 경우 주차 저장
                .questName(questName)
                .achievementType(achievementType)
                .grantedExp(grantedExp)
                .note(note)
                .assignedDate(assignedDate)
                .condition(condition) // ✅ 퀘스트 조건 설정
                .build();
    }


    // ✅ 업데이트 메서드 추가
    public void updateQuest(String achievementType, int grantedExp, String note, LocalDate assignedDate) {
        this.achievementType = achievementType;
        this.grantedExp = grantedExp;
        this.note = note;
        this.assignedDate = assignedDate;
    }
}
