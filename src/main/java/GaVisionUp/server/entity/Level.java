package GaVisionUp.server.entity;

import GaVisionUp.server.entity.enums.JobGroup;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "level") // ✅ 테이블명 level로 설정
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobGroup jobGroup; // 직군 (F, B, G, T)

    @Column(nullable = false, unique = true)
    private String levelName; // 레벨 이름 (예: F1-Ⅰ, B3, G2, T4)

    @Column(nullable = false)
    private int requiredExp; // 해당 레벨 필요 경험치

    public Level(JobGroup jobGroup, String levelName, int requiredExp) {
        this.jobGroup = jobGroup;
        this.levelName = levelName;
        this.requiredExp = requiredExp;
    }

    // ✅ 다음 레벨 경험치 요구량 가져오기
    public int getMinExp() {
        return this.requiredExp;
    }

    public void updateRequiredExp(Integer requiredExp) {
        this.requiredExp = requiredExp;
    }

    public static Level create(JobGroup jobGroup, String levelName, Integer requiredExp) {
        return Level.builder()
                .jobGroup(jobGroup)
                .levelName(levelName)
                .requiredExp(requiredExp)
                .build();
    }
}
