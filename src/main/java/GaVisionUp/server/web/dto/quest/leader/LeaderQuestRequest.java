package GaVisionUp.server.web.dto.quest.leader;

import GaVisionUp.server.entity.enums.Cycle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter // ✅ JSON 변환 시 필수
@NoArgsConstructor
public class LeaderQuestRequest {
    private Long userId;
    private Cycle cycle;
    private String questName;
    private int month;
    private Integer week;
    private String achievementType;
    private String note;
    private LocalDate assignedDate;
}
