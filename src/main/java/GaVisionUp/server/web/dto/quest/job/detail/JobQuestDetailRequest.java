package GaVisionUp.server.web.dto.quest.job.detail;

import GaVisionUp.server.entity.enums.Cycle;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class JobQuestDetailRequest {

    private String department;
    private int part;
    private Cycle cycle;
    private int month; // ✅ 평가 월
    private int round; // ✅ 평가 회차 (주차 대신 round 사용)
    private double sales;
    private double laborCost;
    private LocalDate recordedDate;
}
