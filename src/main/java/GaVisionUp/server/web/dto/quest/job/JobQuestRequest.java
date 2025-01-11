package GaVisionUp.server.web.dto.quest.job;

import GaVisionUp.server.entity.enums.Cycle;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JobQuestRequest {

    private String department; // ✅ 부서명
    private int part; // ✅ 직무 그룹
    private Cycle cycle; // ✅ 주기 (WEEKLY, MONTHLY)
    private int round;
}
