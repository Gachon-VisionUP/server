package GaVisionUp.server.web.dto.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JobQuestRequest {
    private String department;
    private int part;
    private Cycle cycle;
    private int month; // ✅ 월 (1~12)
    private Integer week; // ✅ 주차 (1~5) - 월간 데이터는 null
}
